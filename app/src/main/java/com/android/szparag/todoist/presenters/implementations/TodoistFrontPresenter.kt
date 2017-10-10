package com.android.szparag.todoist.presenters.implementations

import com.android.szparag.todoist.AnimationEvent.AnimationEventType.END
import com.android.szparag.todoist.presenters.contracts.FrontPresenter
import com.android.szparag.todoist.utils.checkNotNull
import com.android.szparag.todoist.utils.ui
import com.android.szparag.todoist.views.contracts.FrontView
import io.reactivex.rxkotlin.subscribeBy

private const val FRONT_LIST_LOADING_THRESHOLD = 4

class TodoistFrontPresenter : TodoistBasePresenter<FrontView>(), FrontPresenter {


  override fun onAttached() {
    logger.debug("onAttached")
    super.onAttached()

  }

  override fun onViewReady() {
    super.onViewReady()
    view?.animateShowBackgroundImage()
        ?.ui()
        ?.filter { (eventType) -> eventType == END }
        ?.flatMap { view?.animateShowQuote()?.ui() }
        ?.filter { (eventType) -> eventType == END }
        ?.flatMap { view?.animatePeekCalendar()?.ui() } //todo: in submodelevents
        ?.filter { (eventType) -> eventType == END }
        ?.subscribe()
        ?.toViewDisposable()

    view?.subscribeDayListScrolls()
        ?.ui()
        ?.subscribeBy(onNext = { scrollEvent ->
          logger.debug("view?.subscribeDayListScrolls.onNext, event: $scrollEvent")
          checkNotNull(scrollEvent.firstVisibleItemPos, scrollEvent.lastVisibleItemPos,
              scrollEvent.lastItemOnListPos) { firstVisibleItemPos, lastVisibleItemPos, lastItemOnListPos ->
            if (FRONT_LIST_LOADING_THRESHOLD >= firstVisibleItemPos || lastVisibleItemPos >= lastItemOnListPos -
                FRONT_LIST_LOADING_THRESHOLD) {
              logger.warn("view?.subscribeDayListScrolls.onNext, event: $scrollEvent")
              logger.warn("view?.subscribeDayListScrolls.onNext, THRESHOLD: (0, $FRONT_LIST_LOADING_THRESHOLD), ($firstVisibleItemPos, " +
                  "$lastVisibleItemPos), (${lastItemOnListPos - FRONT_LIST_LOADING_THRESHOLD}, $lastItemOnListPos)")
            }
          }
        }, onError = { exc ->
          logger.error("view?.subscribeDayListScrolls.onError, exc: $exc")
        }, onComplete = {
          logger.debug("view?.subscribeDayListScrolls.onComplete")
        })
  }

//  private fun calculateFrontListCrossingLoadingThreshold(): Boolean {
//
//  }

  override fun subscribeModelEvents() {
    logger.debug("subscribeModelEvents")
  }

  override fun subscribeViewUserEvents() {
    logger.debug("subscribeViewUserEvents")
  }


}