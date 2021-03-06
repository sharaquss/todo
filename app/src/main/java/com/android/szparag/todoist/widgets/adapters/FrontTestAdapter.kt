package com.android.szparag.todoist.widgets.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.szparag.todoist.R.id
import com.android.szparag.todoist.R.layout
import com.android.szparag.todoist.models.entities.RenderDay
import com.android.szparag.todoist.utils.Logger
import com.android.szparag.todoist.utils.bindView
import com.android.szparag.todoist.utils.emptyMutableList
import com.android.szparag.todoist.utils.setViewDimensions
import com.android.szparag.todoist.widgets.adapters.FrontTestAdapter.DayViewHolder

class FrontTestAdapter(private val itemWidth: Int? = null, private val itemHeight: Int? = null) :
    RecyclerView.Adapter<DayViewHolder>() {

  private val logger by lazy { Logger.create(this::class.java, hashCode()) }
  private var daysList = emptyMutableList<RenderDay>()
  private var layoutInflater: LayoutInflater? = null
    private set(value) {
      if (value == null) return else field = value
    }

  private fun getLayoutInflater(context: Context): LayoutInflater {
    logger.debug("getLayoutInflater, inflater: $layoutInflater")
    if (layoutInflater == null) {
      layoutInflater = LayoutInflater.from(context)
    }
    return layoutInflater!!
  }


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DayViewHolder(
      getLayoutInflater(parent.context)
          .inflate(layout.item_recycler_front_day, parent, false)
          .apply { setViewDimensions(itemWidth, itemHeight) }
  )


  override fun getItemCount() = daysList.size

  override fun getItemId(position: Int) = daysList[position].unixTimestamp

  fun updateData(updatedDaysList: Collection<RenderDay>, fromIndex: Int, changedElementsCount: Int) {
    logger.debug("updateData: $updatedDaysList, fromIndex: $fromIndex, changedElementsCount: $changedElementsCount")
    daysList.addAll(fromIndex, updatedDaysList)
    notifyItemRangeInserted(fromIndex, changedElementsCount)
  }

  override fun onBindViewHolder(holder: DayViewHolder?, position: Int) {
    val item = daysList[position]
    logger.debug("onBindViewHolder, day: $item, position: $position, holder: $holder")
    holder?.let {
      it.dateHeaderText.text = item.dayName
      //todo: make this an internationalized string in xml resources
      it.dateFullText.text = "${item.dayNumber} ${item.monthName} ${item.yearNumber}"
      it.tasksCompletedText.text = item.tasksCompletedCount.toString()
      it.tasksRemainingText.text = item.tasksRemainingCount.toString()
      it.tasksOverviewAdapter.updateData(item.tasksList)
    }
  }


  class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dateHeaderText: TextView by bindView(id.dateHeaderText)
    val dateFullText: TextView by bindView(id.dateFullText)
    val tasksCompletedText: TextView by bindView(id.tasksCompletedText)
    val tasksRemainingText: TextView by bindView(id.tasksRemainingText)
    val tasksOverviewRecycler: RecyclerView by bindView(id.tasksOverviewList)
    val tasksOverviewAdapter: FrontTasksAdapter = FrontTasksAdapter()

    init {
      tasksOverviewRecycler.adapter = tasksOverviewAdapter
      tasksOverviewRecycler.layoutManager = LinearLayoutManager(itemView.context)
    }
  }

}