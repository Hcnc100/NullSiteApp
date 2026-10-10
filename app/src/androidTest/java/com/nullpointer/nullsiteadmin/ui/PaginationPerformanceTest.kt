package com.nullpointer.nullsiteadmin.ui

import androidx.lifecycle.SavedStateHandle
import androidx.test.platform.app.InstrumentationRegistry
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepository
import com.nullpointer.nullsiteadmin.ui.screens.email.emial.viewModel.EmailsViewModel
import com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.viewModel.ProjectViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicInteger

class PaginationPerformanceTest {
    @Test fun projectRejectsDuplicatePageAndPageDuringRefresh() {
        val pages = AtomicInteger()
        lateinit var vm: ProjectViewModel
        onMain {
            vm = ProjectViewModel(SavedStateHandle(), repository<ProjectRepository>(pages))
            assertNull(vm.concatenateProject())
        }
        waitUntil { !vm.isRequestProject }
        var job: Job? = null
        onMain { job = vm.concatenateProject(); assertNull(vm.concatenateProject()) }
        runBlocking { job!!.join() }
        assertEquals(1, pages.get())
        onMain { assertNull(vm.concatenateProject()) } // zero results disables pagination
    }

    @Test fun emailRejectsDuplicatePageAndPageDuringRefresh() {
        val pages = AtomicInteger()
        lateinit var vm: EmailsViewModel
        onMain {
            vm = EmailsViewModel(SavedStateHandle(), repository<EmailsRepository>(pages))
            assertNull(vm.concatenateEmails())
        }
        waitUntil { !vm.isRequestEmail }
        var job: Job? = null
        onMain { job = vm.concatenateEmails(); assertNull(vm.concatenateEmails()) }
        runBlocking { job!!.join() }
        assertEquals(1, pages.get())
        onMain { assertNull(vm.concatenateEmails()) }
    }

    private fun onMain(block: () -> Unit) =
        InstrumentationRegistry.getInstrumentation().runOnMainSync(block)

    private fun waitUntil(condition: () -> Boolean) {
        repeat(100) {
            var ready = false
            onMain { ready = condition() }
            if (ready) return
            Thread.sleep(20)
        }
        error("Refresh did not finish")
    }

    private inline fun <reified T> repository(pages: AtomicInteger): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ ->
            when {
                method.name.startsWith("getList") -> emptyFlow<Nothing>()
                method.name.startsWith("requestLast") -> 1
                method.name.startsWith("concatenate") -> { pages.incrementAndGet(); 0 }
                else -> error("Unexpected repository operation: ${method.name}")
            }
        } as T
}
