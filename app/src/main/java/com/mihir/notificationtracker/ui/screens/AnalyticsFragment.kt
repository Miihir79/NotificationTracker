package com.mihir.notificationtracker.ui.screens

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.mihir.notificationtracker.R
import com.mihir.notificationtracker.databinding.FragmentAnalyticsBinding
import com.mihir.notificationtracker.ui.vm.ViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AnalyticsFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this)[ViewModel::class.java] }
    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private val dayFormat = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()
        setupFilters()
        observeData()
    }

    private fun setupFilters() {
        // App Selector
        viewModel.allUniquePackageNames.observe(viewLifecycleOwner) { packages ->
            val appList = ArrayList<String>()
            appList.add(getString(R.string.all_apps))
            appList.addAll(packages)
            
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, appList)
            binding.atvAppSelector.setAdapter(adapter)
            // Ensure the text matches current selection
            val currentPkg = viewModel.selectedPackageName.value
            binding.atvAppSelector.setText(currentPkg ?: getString(R.string.all_apps), false)
        }

        binding.atvAppSelector.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as String
            viewModel.setSelectedPackage(selected)
        }

        // Day Selector Slider
        binding.btnPrevDay.setOnClickListener {
            changeDay(-1)
        }

        binding.btnNextDay.setOnClickListener {
            changeDay(1)
        }

        binding.btnResetHour.setOnClickListener {
            viewModel.setSelectedHour(null)
            binding.hourlyChart.highlightValues(null)
        }
        
        // Initial date display
        updateDateDisplay()
    }

    private fun changeDay(amount: Int) {
        val range = viewModel.timeRange.value ?: return
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = range.first
        calendar.add(Calendar.DAY_OF_YEAR, amount)
        
        val today = Calendar.getInstance()
        if (calendar.after(today)) return // Don't allow going beyond today

        // Set to start of new day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        
        // Set to end of new day
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val end = calendar.timeInMillis
        
        viewModel.setTimeRange(start, end)
        updateDateDisplay()
    }

    private fun updateDateDisplay() {
        val range = viewModel.timeRange.value ?: return
        val currentDay = range.first
        binding.tvSelectedDay.text = dayFormat.format(Date(currentDay))
        
        // Disable next button if we are at today
        val today = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentDay
        
        val isToday = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                      calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        
        binding.btnNextDay.isEnabled = !isToday
        binding.btnNextDay.alpha = if (isToday) 0.3f else 1.0f
    }

    private fun setupCharts() {
        // Setup Hourly Bar Chart
        binding.hourlyChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setTouchEnabled(true)
            setPinchZoom(false)
            setDrawValueAboveBar(true)

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let {
                        viewModel.setSelectedHour(it.x.toInt())
                    }
                }

                override fun onNothingSelected() {
                    viewModel.setSelectedHour(null)
                }
            })
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(true)
                axisLineColor = Color.LTGRAY
                granularity = 4f
                textColor = Color.GRAY
                textSize = 10f
            }
            
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#EEEEEE")
                setDrawAxisLine(false)
                textColor = Color.GRAY
                textSize = 10f
                axisMinimum = 0f
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = false
        }

        // Setup App Pie Chart
        binding.appChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 70f // Increased for more space in center
            transparentCircleRadius = 73f
            setDrawCenterText(true)
            
            legend.apply {
                isEnabled = true
                verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                textColor = Color.GRAY
                textSize = 11f
                form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE
                isWordWrapEnabled = true
            }
            
            setEntryLabelColor(Color.TRANSPARENT)
        }
    }

    private fun observeData() {
        viewModel.filteredNotificationTimes.observe(viewLifecycleOwner) { times ->
            updateHourlyChart(times)
            updateSummaryStats(times)
        }

        viewModel.filteredTopApps.observe(viewLifecycleOwner) { stats ->
            updateAppChart(stats)
        }
        
        viewModel.selectedPackageName.observe(viewLifecycleOwner) { pkg ->
            binding.cardTopApps.visibility = if (pkg == null) View.VISIBLE else View.GONE
        }

        viewModel.selectedHour.observe(viewLifecycleOwner) { hour ->
            val title = if (hour == null) {
                getString(R.string.top_apps)
            } else {
                "${getString(R.string.top_apps)} at ${String.format(Locale.getDefault(), "%02d:00", hour)}"
            }
            binding.tvTopAppsTitle.text = title
            binding.btnResetHour.visibility = if (hour == null) View.GONE else View.VISIBLE
        }
    }

    private fun updateSummaryStats(times: List<Long>) {
        if (times.isEmpty()) {
            binding.tvTotalCount.text = "0"
            binding.tvDailyAvg.text = "0"
            binding.tvPeakHour.text = "--"
            binding.appChart.centerText = ""
            return
        }

        // Total
        binding.tvTotalCount.text = times.size.toString()
        
        // Center text for Donut
        binding.appChart.centerText = "Total\n${times.size}"
        binding.appChart.setCenterTextSize(16f)
        binding.appChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)

        // Daily Average (In the context of single day selected, it's just the count)
        binding.tvDailyAvg.text = times.size.toString()

        val hourlyCounts = IntArray(24)
        val calendar = Calendar.getInstance()
        times.forEach { time ->
            calendar.timeInMillis = time
            hourlyCounts[calendar.get(Calendar.HOUR_OF_DAY)]++
        }
        val peakHour = hourlyCounts.indices.maxByOrNull { hourlyCounts[it] } ?: 0
        binding.tvPeakHour.text = String.format(Locale.getDefault(), "%02d:00", peakHour)
    }

    private fun updateHourlyChart(times: List<Long>) {
        val hourlyCounts = IntArray(24)
        val calendar = Calendar.getInstance()
        times.forEach { time ->
            calendar.timeInMillis = time
            hourlyCounts[calendar.get(Calendar.HOUR_OF_DAY)]++
        }

        val entries = ArrayList<BarEntry>()
        for (i in 0 until 24) {
            entries.add(BarEntry(i.toFloat(), hourlyCounts[i].toFloat()))
        }

        val dataSet = BarDataSet(entries, "")
        // Use colors from the design: light blue for bars, deep purple for highlight
        dataSet.color = Color.parseColor("#D1D9E6") 
        dataSet.highLightColor = Color.parseColor("#5C6BC0")
        dataSet.setDrawValues(false)
        
        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
        
        binding.hourlyChart.data = barData
        binding.hourlyChart.xAxis.valueFormatter = IndexAxisValueFormatter((0 until 24).map { String.format(Locale.getDefault(), "%02dh", it) })
        binding.hourlyChart.animateY(1000, Easing.EaseOutCubic)
        binding.hourlyChart.invalidate()
    }

    private fun updateAppChart(stats: List<com.mihir.notificationtracker.database.AppUsageStats>) {
        if (stats.isEmpty()) {
            binding.appChart.clear()
            return
        }
        val entries = ArrayList<PieEntry>()
        stats.forEach { stat ->
            val label = stat.packageName.split(".").lastOrNull() ?: stat.packageName
            entries.add(PieEntry(stat.count.toFloat(), label))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.sliceSpace = 2f
        // Material colors matching the design
        dataSet.colors = arrayListOf(
            Color.parseColor("#5C6BC0"), Color.parseColor("#2E7D32"),
            Color.parseColor("#42A5F5"), Color.parseColor("#FF7043"), Color.parseColor("#EC407A")
        )

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(binding.appChart))
        pieData.setValueTextSize(11f)
        pieData.setValueTextColor(Color.WHITE)
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD)

        binding.appChart.data = pieData
        binding.appChart.animateY(1400, Easing.EaseInOutQuad)
        binding.appChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}