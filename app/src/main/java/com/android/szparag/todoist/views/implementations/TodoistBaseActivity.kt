package com.android.szparag.todoist.views.implementations

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.android.szparag.todoist.events.PermissionEvent
import com.android.szparag.todoist.events.PermissionEvent.PermissionResponse
import com.android.szparag.todoist.events.PermissionEvent.PermissionResponse.PERMISSION_DENIED
import com.android.szparag.todoist.presenters.contracts.Presenter
import com.android.szparag.todoist.utils.Logger
import com.android.szparag.todoist.utils.emptyString
import com.android.szparag.todoist.views.contracts.UnixTimestamp
import com.android.szparag.todoist.views.contracts.View
import com.android.szparag.todoist.views.contracts.View.PermissionType
import com.android.szparag.todoist.views.contracts.View.PermissionType.NULL
import com.android.szparag.todoist.views.contracts.View.Screen
import com.android.szparag.todoist.views.contracts.View.UserAlertMessage
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

abstract class TodoistBaseActivity<P : Presenter<*>> : AppCompatActivity(), View {

  override val logger by lazy { Logger.create(this::class.java, this.hashCode()) }
  @Inject lateinit open var presenter: P //todo: close and private this somehow
  override val viewReadySubject: Subject<Boolean> = ReplaySubject.create()
  override val permissionsSubject: Subject<PermissionEvent> = ReplaySubject.create()
  private var defaultUserAlert: Snackbar? = null
  private var windowFocusCache = false

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    viewReadySubject.doOnSubscribe { viewReadySubject.onNext(hasWindowFocus()) }
    logger.debug("logger created, $logger")
    logger.debug("onCreate, bundle: $savedInstanceState")
    viewReadySubject.onNext(true)
  }

  @CallSuper
  override fun onStart() {
    super.onStart()
    logger.debug("onStart")
//    presenter.attach(this@TodoistBaseActivity as Nothing)
  }

  @CallSuper
  override fun onStop() {
    super.onStop()
    logger.debug("onStop")
//    presenter.detach()
  }


  @CallSuper
  override fun setupViews() {
    logger.debug("setupViews")
  }

  override final fun onWindowFocusChanged(hasFocus: Boolean) {
    logger.debug("onWindowFocusChanged, hasFocus: $hasFocus, windowFocusCache: $windowFocusCache")
    super.onWindowFocusChanged(hasFocus)
    if (windowFocusCache != hasFocus) viewReadySubject.onNext(hasFocus)
    windowFocusCache = hasFocus
  }

  override final fun subscribeOnViewReady(): Observable<Boolean> {
    logger.debug("subscribeOnViewReady")
    return viewReadySubject
  }

  //todo: this has to be fixed
  override final fun gotoScreen(targetScreen: Screen) {
    logger.debug("gotoScreen, targetScreen: $targetScreen")

  }

  override final fun goToDayScreen(unixTimestamp: UnixTimestamp) {
    startActivity(TodoistDayActivity.prepareDayActivityRoutingIntent(packageContext = this, dayUnixTimestamp = unixTimestamp))
  }

  override fun resolveStartupData() {
    logger.debug("resolveStartupData")
  }

  override final fun checkPermissions(vararg permissions: PermissionType) {
    logger.debug("checkPermissions, permissions: $permissions")
    permissions.forEach {
      val permissionResponseInt = checkSelfPermission(permissionTypeToString(it))
      val permissionResponseToType = permissionResponseToType(permissionResponseInt)
      val permissionEvent = PermissionEvent(it, permissionResponseToType)
      permissionsSubject.onNext(permissionEvent)
    }
  }

  override final fun requestPermissions(vararg permissions: PermissionType) {
    logger.debug("requestPermissions, permissions: $permissions")
    requestPermissions(permissions.map(this::permissionTypeToString).toTypedArray(), requestCode())
  }

  override final fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    logger.debug("onRequestPermissionsResult, requestCode: $requestCode, permissions: $permissions, results: $grantResults")
  }

  override fun renderUserAlertMessage(userAlertMessage: UserAlertMessage) {
    logger.debug("renderUserAlertMessage, alert: $userAlertMessage")
  }

  override fun stopRenderUserAlertMessage(userAlertMessage: UserAlertMessage) {
    logger.debug("stopRenderUserAlertMessage, alert: $userAlertMessage")
    defaultUserAlert?.dismiss() //omitting userAlertMessage check, since Snackbars can be dismissed manually any time.
  }

  override final fun subscribeForPermissionsChange(): Observable<PermissionEvent> {
    logger.debug("requestPermissions")
    return permissionsSubject
  }

  private fun permissionTypeToString(permissionType: PermissionType): String {
    logger.debug("permissionTypeToString, permissionType: $permissionType")
    return emptyString()
  }

  private fun permissionStringToType(permissionString: String): PermissionType {
    logger.debug("permissionStringToType, permissionString: $permissionString")
    return NULL
  }

  private fun permissionResponseToType(permissionResponseInt: Int): PermissionResponse {
    logger.debug("permissionResponseToType, permissionResponseInt: $permissionResponseInt")
    return PERMISSION_DENIED
  }

  private fun requestCode() = Math.abs(this.packageName.hashCode())

}