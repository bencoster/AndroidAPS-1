package info.nightscout.androidaps.plugins.general.automation.actions

import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.Constants
import info.nightscout.androidaps.TestBaseWithProfile
import info.nightscout.androidaps.TestPumpPlugin
import info.nightscout.androidaps.data.PumpEnactResult
import info.nightscout.androidaps.interfaces.*
import info.nightscout.androidaps.logging.AAPSLogger
import info.nightscout.androidaps.logging.UserEntryLogger
import info.nightscout.androidaps.plugins.bus.RxBusWrapper
import info.nightscout.androidaps.plugins.general.automation.triggers.Trigger
import info.nightscout.androidaps.utils.resources.ResourceHelper
import info.nightscout.androidaps.utils.sharedPreferences.SP
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.powermock.core.classloader.annotations.PrepareForTest

@PrepareForTest(RxBusWrapper::class, ActionsTestBase.TestLoopPlugin::class, UserEntryLogger::class)
open class ActionsTestBase : TestBaseWithProfile() {

    open class TestLoopPlugin(
        aapsLogger: AAPSLogger,
        resourceHelper: ResourceHelper,
        injector: HasAndroidInjector,
        pluginDescription: PluginDescription
    ) : PluginBase(
        pluginDescription, aapsLogger, resourceHelper, injector
    ), LoopInterface {

        private var suspended = false
        override var lastRun: LoopInterface.LastRun? = LoopInterface.LastRun()
        override val isSuspended: Boolean = suspended
        override fun suspendTo(endTime: Long) {}
        override fun createOfflineEvent(durationInMinutes: Int) {}
        override fun suspendLoop(durationInMinutes: Int) {}
    }

    @Mock lateinit var sp: SP
    @Mock lateinit var commandQueue: CommandQueueProvider
    @Mock lateinit var configBuilder: ConfigBuilder
    @Mock lateinit var activePlugin: ActivePlugin
    @Mock lateinit var profilePlugin: ProfileInterface
    @Mock lateinit var smsCommunicatorPlugin: SmsCommunicator
    @Mock lateinit var loopPlugin: TestLoopPlugin
    @Mock lateinit var uel: UserEntryLogger

    private val pluginDescription = PluginDescription()
    lateinit var testPumpPlugin: TestPumpPlugin

    var injector: HasAndroidInjector = HasAndroidInjector {
        AndroidInjector {
            if (it is ActionStopTempTarget) {
                it.aapsLogger = aapsLogger
                it.resourceHelper = resourceHelper
                it.dateUtil = dateUtil
                it.repository = repository
                it.uel = uel
            }
            if (it is ActionStartTempTarget) {
                it.aapsLogger = aapsLogger
                it.resourceHelper = resourceHelper
                it.activePlugin = activePlugin
                it.repository = repository
                it.profileFunction = profileFunction
                it.uel = uel
                it.dateUtil = dateUtil
            }
            if (it is ActionSendSMS) {
                it.aapsLogger = aapsLogger
                it.resourceHelper = resourceHelper
                it.smsCommunicatorPlugin = smsCommunicatorPlugin
            }
            if (it is ActionProfileSwitch) {
                it.aapsLogger = aapsLogger
                it.resourceHelper = resourceHelper
                it.activePlugin = activePlugin
                it.profileFunction = profileFunction
                it.uel = uel
                it.dateUtil = dateUtil
            }
            if (it is ActionProfileSwitchPercent) {
                it.resourceHelper = resourceHelper
                it.activePlugin = activePlugin
                it.uel = uel
            }
            if (it is ActionNotification) {
                it.resourceHelper = resourceHelper
                it.rxBus = rxBus
            }
            if (it is ActionLoopSuspend) {
                it.loopPlugin = loopPlugin
                it.resourceHelper = resourceHelper
                it.rxBus = rxBus
                it.uel = uel
            }
            if (it is ActionLoopResume) {
                it.loopPlugin = loopPlugin
                it.resourceHelper = resourceHelper
                it.configBuilder = configBuilder
                it.rxBus = rxBus
                it.uel = uel
            }
            if (it is ActionLoopEnable) {
                it.loopPlugin = loopPlugin
                it.resourceHelper = resourceHelper
                it.configBuilder = configBuilder
                it.rxBus = rxBus
                it.uel = uel
            }
            if (it is ActionLoopDisable) {
                it.loopPlugin = loopPlugin
                it.resourceHelper = resourceHelper
                it.configBuilder = configBuilder
                it.commandQueue = commandQueue
                it.rxBus = rxBus
                it.uel = uel
            }
            if (it is PumpEnactResult) {
                it.resourceHelper = resourceHelper
            }
            if (it is Trigger) {
                it.resourceHelper = resourceHelper
                it.profileFunction = profileFunction
            }
        }
    }

    @Before
    fun mock() {
        testPumpPlugin = TestPumpPlugin(pluginDescription, aapsLogger, resourceHelper, injector)
        `when`(activePlugin.activePump).thenReturn(testPumpPlugin)
        `when`(profileFunction.getUnits()).thenReturn(Constants.MGDL)
        `when`(activePlugin.activeProfileInterface).thenReturn(profilePlugin)
        `when`(profilePlugin.profile).thenReturn(getValidProfileStore())
    }
}