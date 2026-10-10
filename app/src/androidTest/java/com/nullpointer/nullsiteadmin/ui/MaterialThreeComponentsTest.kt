package com.nullpointer.nullsiteadmin.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableImg
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import com.nullpointer.nullsiteadmin.ui.screens.home.componets.ItemNavDrawer
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.EditInfoProfileScreen
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.actions.EditInfoProfileActions
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components.ButtonUpdateProject
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberEditInfoProfileState
import com.nullpointer.nullsiteadmin.ui.share.*
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import kotlinx.coroutines.launch
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Local production components only: no Firebase, login or image picker launch. */
@OptIn(ExperimentalMaterial3Api::class)
@RunWith(Parameterized::class)
class MaterialThreeComponentsTest(private val dark: Boolean) {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    companion object {
        @JvmStatic @Parameterized.Parameters(name = "dark={0}")
        fun themes() = listOf(arrayOf(false), arrayOf(true))
    }

    @Test fun drawerOpensSelectsAndCloses() {
        var selected = false
        rule.setContent {
            NullSiteAdminTheme(darkTheme = dark) {
                val drawer = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                ModalNavigationDrawer(drawerState = drawer, drawerContent = {
                    ModalDrawerSheet {
                        ItemNavDrawer(MainDestinations.ProjectsScreen, selected) {
                            selected = true
                            scope.launch { drawer.close() }
                        }
                    }
                }) {
                    Button(onClick = { scope.launch { drawer.open() } }) { Text("Open drawer") }
                }
            }
        }
        rule.onNodeWithText("Open drawer").performClick()
        rule.onNodeWithText(MainDestinations.ProjectsScreen.label).assertIsDisplayed().performClick()
        rule.waitForIdle()
        rule.runOnIdle { assertTrue(selected) }
        rule.onNodeWithText(MainDestinations.ProjectsScreen.label).assertIsNotDisplayed()
    }

    @Test fun profileSheetCanBeOpenedDismissedAndReopened() {
        rule.setContent {
            NullSiteAdminTheme(darkTheme = dark) {
                val state = rememberEditInfoProfileState(actionCropSuccess = {}, actionCropFailure = {})
                val name = remember { field("name") }
                val profession = remember { field("profession") }
                val description = remember { field("description") }
                EditInfoProfileScreen(
                    isLoading = false, isDataValid = true,
                    snackbarHostState = state.snackbarHostState,
                    sheetState = state.modalState, sheetVisible = state.isModalVisible,
                    imageProfile = remember { PropertySavableImg("image", SavedStateHandle()) },
                    nameAdmin = name, professionAdmin = profession, descriptionAdmin = description,
                    actionBeforeSelect = {},
                    onEditInfoProfileActions = {
                        when (it) {
                            EditInfoProfileActions.SHOW_BOTTOM_SHEET -> state.showBottomSheet()
                            EditInfoProfileActions.HIDDEN_BOTTOM_SHEET -> state.hiddenBottomSheet()
                            else -> Unit
                        }
                    },
                )
            }
        }
        val edit = rule.activity.getString(R.string.description_edit_img_profile)
        val gallery = rule.activity.getString(R.string.option_img_gallery)
        repeat(2) {
            rule.onNodeWithContentDescription(edit).performScrollTo().performClick()
            rule.onNodeWithText(gallery).assertIsDisplayed()
            rule.onNodeWithText(rule.activity.getString(R.string.option_img_camera)).assertIsDisplayed()
            Espresso.pressBack()
            rule.waitForIdle()
            rule.onNodeWithText(gallery).assertDoesNotExist()
        }
    }

    @Test fun refreshGestureUsesLatestCallbackAndLoadingDisablesGestures() {
        val version = mutableIntStateOf(1)
        val enabled = mutableStateOf(true)
        var first = 0
        var latest = 0
        rule.setContent {
            NullSiteAdminTheme(darkTheme = dark) {
                val currentVersion = version.intValue
                val refresh = rememberAppRefreshState(false) {
                    if (currentVersion == 1) first++ else latest++
                }
                AppPullToRefreshBox(refresh, Modifier.fillMaxSize().testTag("refresh"), enabled.value) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(20) { Text("Item $it", Modifier.height(100.dp)) }
                    }
                }
            }
        }
        rule.onNodeWithTag("refresh").performTouchInput { swipeDown(durationMillis = 600) }
        rule.runOnIdle { assertEquals(1, first); version.intValue = 2 }
        rule.onNodeWithTag("refresh").performTouchInput { swipeDown(durationMillis = 600) }
        rule.runOnIdle { assertEquals(1, latest); enabled.value = false }
        rule.onNodeWithTag("refresh").performTouchInput { swipeDown(durationMillis = 600) }
        rule.runOnIdle { assertEquals(1, first); assertEquals(1, latest) }
    }

    @Test fun snackbarIsHostedByMaterialThreeScaffold() {
        rule.setContent {
            NullSiteAdminTheme(darkTheme = dark) {
                val host = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                Scaffold(snackbarHost = { SnackbarHost(host) }) { padding ->
                    Button(modifier = Modifier.padding(padding), onClick = {
                        scope.launch { host.showSnackbar("Saved locally", duration = SnackbarDuration.Indefinite) }
                    }) { Text("Show message") }
                }
            }
        }
        rule.onNodeWithText("Show message").performClick()
        rule.onNodeWithText("Saved locally").assertIsDisplayed()
    }

    @Test fun passwordVisibilityErrorsAndDisabledSaveRemainCorrect() {
        val password = field("password")
        var saved = false
        rule.setContent {
            NullSiteAdminTheme(darkTheme = dark) {
                Column {
                    PasswordTextSavable(valueProperty = password)
                    ButtonUpdateProject(isEnable = false, actionClick = { saved = true })
                }
            }
        }
        val show = rule.activity.getString(R.string.description_show_password)
        val hide = rule.activity.getString(R.string.description_hide_password)
        rule.onNodeWithContentDescription(show).performClick()
        rule.onNodeWithContentDescription(hide).assertIsDisplayed().performClick()
        rule.onNodeWithContentDescription(show).assertIsDisplayed()
        rule.runOnIdle { password.changeValue("") }
        rule.onNodeWithText(rule.activity.getString(R.string.error_empty_example)).assertIsDisplayed()
        rule.runOnIdle { password.changeValue("12345678901") }
        rule.onNodeWithText(rule.activity.getString(R.string.error_length_example)).assertIsDisplayed()
        rule.onNodeWithText("11/10").assertIsDisplayed()
        rule.onNodeWithText(rule.activity.getString(R.string.text_update_project)).assertIsNotEnabled()
        rule.runOnIdle { assertFalse(saved) }
    }

    private fun field(tag: String) = PropertySavableString(
        tag, R.string.hint_example, R.string.label_example, 10, SavedStateHandle(), "Sample",
        R.string.error_empty_example, R.string.error_length_example,
    )
}
