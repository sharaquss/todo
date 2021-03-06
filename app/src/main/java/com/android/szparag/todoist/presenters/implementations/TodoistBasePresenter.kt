package com.android.szparag.todoist.presenters.implementations

import android.support.annotation.CallSuper
import com.android.szparag.todoist.models.contracts.Model
import com.android.szparag.todoist.presenters.contracts.Presenter
import com.android.szparag.todoist.utils.Logger
import com.android.szparag.todoist.utils.add
import com.android.szparag.todoist.utils.ui
import com.android.szparag.todoist.views.contracts.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

abstract class TodoistBasePresenter<V : View, out M : Model>(val model: M) : Presenter<V> {

  override val logger by lazy { Logger.create(this::class.java, this.hashCode()) }
  override var view: V? = null
  override lateinit var viewDisposables: CompositeDisposable //todo: i can access this in implemented presenters, that's bad
  override lateinit var modelDisposables: CompositeDisposable

  //todo: logger creation is probably heavy, move to another thread
  override fun attach(view: V) {
    logger.info("attach.action, view: $view")
    this.view = view
    model.attach().subscribe { onAttached() }
  }

  @CallSuper override fun onAttached() {
    logger.info("onAttached")
    viewDisposables = CompositeDisposable()
    modelDisposables = CompositeDisposable()
    subscribeModelEvents()
    subscribeViewReadyEvents()
    subscribeViewPermissionsEvents()
    subscribeViewUserEvents()
  }


  override final fun detach() {
    logger.info("detach")
    onBeforeDetached()
    view = null
  }

  @CallSuper override fun onBeforeDetached() {
    logger.info("onBeforeDetached")
    viewDisposables.clear()
    modelDisposables.clear()
    model.detach()
  }

  override fun onViewReady() {
    logger.info("onViewReady")
    view?.setupViews()
  }

  @CallSuper override fun subscribeViewPermissionsEvents() {
    logger.debug("subscribeViewPermissionsEvents")
  }

  private fun subscribeViewReadyEvents() {
    logger.debug("subscribeViewReadyEvents")
    view
        ?.subscribeOnViewReady()
        ?.ui()
        ?.filter { readyFlag -> readyFlag }
        ?.subscribeBy(
            onNext = { readyFlag ->
              logger.debug("subscribeViewReadyEvents.onNext, ready: $readyFlag")
              onViewReady()
            },
            onComplete = {
              logger.debug("subscribeViewReadyEvents.onComplete")
            },
            onError = { exc ->
              logger.error("subscribeViewReadyEvents.onError", exc)
            }
        )
  }


  fun Disposable?.toViewDisposable() {
    logger.debug("toViewDisposable: viewDisposables: $viewDisposables, disposed: ${viewDisposables.isDisposed}")
    viewDisposables.takeIf { !it.isDisposed }?.add(this)
  }

  fun Disposable?.toModelDisposable() {
    logger.debug("toModelDisposable: modelDisposables: $modelDisposables, disposed: ${modelDisposables.isDisposed}")
    modelDisposables.takeIf { !it.isDisposed }?.add(this)
  }
}