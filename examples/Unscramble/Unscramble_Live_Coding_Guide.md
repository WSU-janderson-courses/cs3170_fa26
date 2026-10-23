# Unscramble: ViewModel and State

**CS 3170 — Mobile Application Development**  
**Live-coding walkthrough** (based on *8_ViewModelAndArchitecture.pptx*)

> **How to use this guide:** Work down the page while keeping Android Studio open. Each step tells you what to change, why it matters, and what students should be able to observe. The code blocks are *incremental edits* to the provided starter project—not separate, complete files unless labeled otherwise. Keep the starter project's existing UI/layout code and resources.

## Starting point: What are we building?

The starter project already draws the **Unscramble** game. Our job is to give the UI working game logic.

- Show a shuffled word and let the player type a guess.
- **Submit:** check the guess; award points and move to the next word when correct.
- **Incorrect guess:** show an error without increasing the score.
- **Skip:** move to another word without awarding points.
- Display the **score** and **word count**.
- End after **10 words**, and offer **Play Again**.
- Preserve the current game when the device rotates.
- Test the game logic separately from the UI.

### Locate the starter files (slide 8)

| File / component | Responsibility |
| --- | --- |
| `data/WordsData.kt` | The word list and game constants such as `MAX_NO_OF_WORDS` and `SCORE_INCREASE` |
| `MainActivity.kt` | Sets up the app and displays `GameScreen` |
| `ui/GameScreen.kt` | Compose UI; initially mostly layout |
| `GameStatus` | Displays the score |
| `GameLayout` | Displays the word, instructions, and text input |
| `GameScreen` | Combines the pieces with Submit and Skip buttons |
| `FinalScoreDialog` | End-of-game dialog with Play Again |

**Before coding:** Run the starter app and point out which parts are currently static. Find each composable in `GameScreen.kt` before introducing new classes.

---

## Part 1 — Move app state out of the UI

### 1. Introduce the architecture (slides 2–6)

**Explain briefly:** An app needs rules for how responsibilities are divided. A useful division is:

- **UI layer:** Compose renders what the player sees and sends user events.
- **State holder (`ViewModel`):** Stores the game state and applies game rules.
- **Data:** Provides words and constants (`WordsData.kt`).

This is **separation of concerns**: the UI should not be responsible for choosing words, calculating scores, or tracking which words have been used.

**Mental model:**

```text
       state (data) flows down
   GameViewModel ────────────────> GameScreen / GameLayout
       ^                                   |
       |                                   |
       └──────── user events flow up ──────┘
          Submit, Skip, text changes
```

**Key distinction:** *UI* means what is currently on screen. *UI state* means the data describing what the UI should display. We change the state, and Compose updates the UI.

**Ask:** If the phone rotates, which object should remember the current score: a text composable or the `ViewModel`?

### 2. Add the `ViewModel` dependency (slides 9, 16)

In **`app/build.gradle.kts`**, check the dependencies block. The slide deck uses:

```kotlin
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
```

Sync Gradle. Some starter templates already supply the dependency through a version catalog; avoid adding it twice. The deck also mentions the Lifecycle runtime dependency. Use the project's existing runtime setup if it is already configured.

Create two files under `ui/`:

```text
ui/
├── GameUiState.kt
├── GameViewModel.kt
└── GameScreen.kt
```

### 3. Define immutable UI state (slides 6, 9)

**`GameUiState.kt`** — start with one property:

```kotlin
data class GameUiState(
    val currentScrambledWord: String = ""
)
```

**Explain:** A `data class` is a convenient description of the screen's state. Properties are `val` so code cannot directly alter an existing state object. Later, we'll use `copy()` to make an updated object.

**Ask:** Why might we prefer to describe *the whole game screen* with one state object rather than having every composable maintain its own version of the data?

### 4. Create a `ViewModel` and expose a `StateFlow` (slide 10)

**`GameViewModel.kt`** — start with:

```kotlin
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())

    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
}
```

**Point out:**

- `MutableStateFlow` holds a current value and emits changes.
- `_uiState` is **private and mutable**: only this class can change it.
- `uiState` is **public and read-only**: the UI can observe it but cannot change it directly.
- This is a *backing property* pattern.

**Quick check:** `GameViewModel` should compile, but the screen has not changed yet.

---

## Part 2 — Choose and display a word

### 5. Track the current word and previously used words (slide 11)

Add these members inside `GameViewModel`:

```kotlin
// The real (unscrambled) answer.
private lateinit var currentWord: String

// Answers already used during this game.
private var usedWords: MutableSet<String> = mutableSetOf()
```

**Explain:** The player should see the *scrambled* word, but the `ViewModel` also needs to remember the original answer. A set helps avoid repeated words.

### 6. Pick a random word and shuffle it (slide 11)

Add these functions to `GameViewModel`. Import `allWords` from the starter's `WordsData.kt` as needed.

```kotlin
private fun pickRandomWordAndShuffle(): String {
    currentWord = allWords.random()

    if (usedWords.contains(currentWord)) {
        return pickRandomWordAndShuffle()
    } else {
        usedWords.add(currentWord)
        return shuffleCurrentWord(currentWord)
    }
}

private fun shuffleCurrentWord(word: String): String {
    val tempWord = word.toCharArray()
    tempWord.shuffle()

    // Avoid showing the answer unchanged.
    while (String(tempWord) == word) {
        tempWord.shuffle()
    }

    return String(tempWord)
}
```

**Talk through the algorithm:**

1. Select an answer at random.
2. If we've already used it, select again.
3. Otherwise, record it and shuffle its letters.
4. If shuffling accidentally produces the original word, shuffle again.

**Teaching note:** This follows the slides' approach. It assumes the word list has enough unique words, and that every word has at least two distinguishable arrangements.

### 7. Initialize or reset the game (slide 11)

Add this to `GameViewModel`:

```kotlin
fun resetGame() {
    usedWords.clear()
    _uiState.value = GameUiState(
        currentScrambledWord = pickRandomWordAndShuffle()
    )
}

init {
    resetGame()
}
```

**Explain:** The `init` block executes when the `ViewModel` is created. We immediately choose the first word and publish it in the UI state. Calling `resetGame()` later starts a fresh game.

**Check:** Inspect `uiState.value.currentScrambledWord` in the debugger or logs if you want to show that the data exists before anything is connected to the UI.

### 8. Observe the state in `GameScreen` (slides 12–15)

Inside `GameScreen.kt`, give the top-level composable access to the `ViewModel`:

```kotlin
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel()
) {
    val gameUiState by gameViewModel.uiState.collectAsState()

    // Keep the starter's existing screen layout below.
}
```

Pass the word into the existing `GameLayout` call:

```kotlin
GameLayout(
    currentScrambledWord = gameUiState.currentScrambledWord,
    modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(mediumPadding)
)
```

Update `GameLayout` to accept the value and display it where the hard-coded word used to be:

```kotlin
@Composable
fun GameLayout(
    currentScrambledWord: String,
    modifier: Modifier = Modifier
) {
    // In the starter's word Text(...):
    Text(
        text = currentScrambledWord,
        style = typography.displayMedium
    )

    // Keep the rest of the starter layout.
}
```

**Explain the flow:** `StateFlow` emits a new value → `collectAsState()` observes it → Compose recomposes the places that read it.

**Checkpoint:** Run the app. The displayed scrambled word should now vary between newly started games, rather than being hard-coded.

---

## Part 3 — Send input events back to the ViewModel

### 9. Store the player's guess (slides 18–20)

Not all state needs to live in `GameUiState`. For this exercise, the `ViewModel` holds the current typed guess as Compose state.

In `GameViewModel`:

```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

var userGuess by mutableStateOf("")
    private set

fun updateUserGuess(guessedWord: String) {
    userGuess = guessedWord
}
```

**Explain:** The UI may read `userGuess`, but `private set` requires the UI to call `updateUserGuess(...)` instead of assigning it directly. The `ViewModel` remains responsible for state changes.

### 10. Add event callbacks to `GameLayout` (slides 18–21)

Expand the function parameters:

```kotlin
@Composable
fun GameLayout(
    onUserGuessChanged: (String) -> Unit,
    userGuess: String,
    onKeyboardDone: () -> Unit,
    currentScrambledWord: String,
    modifier: Modifier = Modifier
) {
    // Existing UI...
}
```

In the existing `OutlinedTextField`, replace the hard-coded field value and wire the events:

```kotlin
OutlinedTextField(
    value = userGuess,
    onValueChange = onUserGuessChanged,
    singleLine = true,
    // Keep existing modifier, colors, shape, and label.
    keyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Done
    ),
    keyboardActions = KeyboardActions(
        onDone = { onKeyboardDone() }
    )
)
```

Pass the values and callbacks down from `GameScreen`:

```kotlin
GameLayout(
    onUserGuessChanged = { gameViewModel.updateUserGuess(it) },
    userGuess = gameViewModel.userGuess,
    onKeyboardDone = {}, // We'll implement this in the next step.
    currentScrambledWord = gameUiState.currentScrambledWord,
    modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(mediumPadding)
)
```

**Ask:** When the player types `cat`, which direction does the event travel? Which direction does the new text value travel?

**Checkpoint:** The text field should let the player type. You have implemented the **event-up / state-down** pattern even though Submit does not yet check the answer.

---

## Part 4 — Validate guesses and update the game

### 11. Add a wrong-guess flag to the UI state (slides 22–23)

Update `GameUiState`:

```kotlin
data class GameUiState(
    val currentScrambledWord: String = "",
    val isGuessedWordWrong: Boolean = false
)
```

In `GameViewModel`, add the first version of `checkUserGuess()`:

```kotlin
import kotlinx.coroutines.flow.update

fun checkUserGuess() {
    if (userGuess.equals(currentWord, ignoreCase = true)) {
        // Correct-answer behavior comes next.
    } else {
        _uiState.update { currentState ->
            currentState.copy(isGuessedWordWrong = true)
        }
    }

    updateUserGuess("")
}
```

**Explain `copy()`:** We create a new `GameUiState` with one changed property, leaving the other properties unchanged. Because `StateFlow` gets a new value, the UI can update.

**Ask:** What happens to `currentScrambledWord` when we call `copy(isGuessedWordWrong = true)`?

### 12. Wire Submit and the keyboard Done key (slide 24)

In `GameScreen`, connect the existing Submit button:

```kotlin
Button(
    modifier = Modifier.fillMaxWidth(),
    onClick = { gameViewModel.checkUserGuess() }
) {
    Text(text = stringResource(R.string.submit))
}
```

Also change the argument passed to `GameLayout`:

```kotlin
onKeyboardDone = { gameViewModel.checkUserGuess() },
```

**Checkpoint:** Type an incorrect word and press either Submit or the keyboard's Done action. Both should call the same `ViewModel` function. The text field will clear.

### 13. Show a validation error in the text field (slides 24–25)

Add `isGuessWrong` to the `GameLayout` parameters and pass:

```kotlin
isGuessWrong = gameUiState.isGuessedWordWrong,
```

Then update the existing `OutlinedTextField`:

```kotlin
OutlinedTextField(
    value = userGuess,
    onValueChange = onUserGuessChanged,
    isError = isGuessWrong,
    label = {
        if (isGuessWrong) {
            Text(stringResource(R.string.wrong_guess))
        } else {
            Text(stringResource(R.string.enter_your_word))
        }
    },
    // Keep the existing styling and keyboard configuration.
)
```

**Checkpoint:** An incorrect submission should clear the text and turn the text field into an error state. We have changed **game state**, not manually painted the border red.

### 14. Add scoring and advance to the next word (slide 26)

Extend `GameUiState`:

```kotlin
val score: Int = 0
```

Add this helper to `GameViewModel`:

```kotlin
private fun updateGameState(updatedScore: Int) {
    _uiState.update { currentState ->
        currentState.copy(
            isGuessedWordWrong = false,
            currentScrambledWord = pickRandomWordAndShuffle(),
            score = updatedScore
        )
    }
}
```

Now replace the placeholder in `checkUserGuess()`'s **correct-answer** branch:

```kotlin
if (userGuess.equals(currentWord, ignoreCase = true)) {
    val updatedScore = _uiState.value.score + SCORE_INCREASE
    updateGameState(updatedScore)
} else {
    _uiState.update { currentState ->
        currentState.copy(isGuessedWordWrong = true)
    }
}
updateUserGuess("")
```

Import `SCORE_INCREASE` from the provided `WordsData.kt` if necessary.

**Explain:** Incorrect answer → error flag. Correct answer → score increases, new word appears, error flag resets. Both cases clear the input field.

### 15. Track the current word count (slides 27–28)

Add to `GameUiState`:

```kotlin
val currentWordCount: Int = 1
```

Update the `copy(...)` call in `updateGameState(...)`:

```kotlin
currentState.copy(
    isGuessedWordWrong = false,
    currentScrambledWord = pickRandomWordAndShuffle(),
    score = updatedScore,
    currentWordCount = currentState.currentWordCount.inc()
)
```

In `GameScreen`, pass both score and count into the existing UI:

```kotlin
GameLayout(
    // ...existing arguments...
    wordCount = gameUiState.currentWordCount,
    // ...
)

GameStatus(
    score = gameUiState.score,
    modifier = Modifier.padding(20.dp)
)
```

Add `wordCount: Int` to `GameLayout` and replace the hard-coded word-count display:

```kotlin
Text(
    text = stringResource(R.string.word_count, wordCount),
    style = typography.titleMedium,
    color = colorScheme.onPrimary
)
```

Keep the starter's background, alignment, and padding modifiers around this text.

**Checkpoint:** Correct guesses should increase the score **and** the word counter. Incorrect guesses should change neither.

### 16. Implement Skip (slide 29)

In `GameViewModel`:

```kotlin
fun skipWord() {
    updateGameState(_uiState.value.score)
    updateUserGuess("")
}
```

Wire the existing Skip button's click event:

```kotlin
OutlinedButton(
    onClick = { gameViewModel.skipWord() },
    modifier = Modifier.fillMaxWidth()
) {
    Text(text = stringResource(R.string.skip))
}
```

**Ask:** Why can we reuse `updateGameState(...)` for Skip? Why do we pass the *current score* instead of adding `SCORE_INCREASE`?

**Checkpoint:** Skip changes the word and advances the count without adding points.

---

## Part 5 — Finish the game and handle rotation

### 17. End the game after 10 words (slide 30)

Add to `GameUiState`:

```kotlin
val isGameOver: Boolean = false
```

Replace `updateGameState(...)` with its final version:

```kotlin
private fun updateGameState(updatedScore: Int) {
    if (usedWords.size == MAX_NO_OF_WORDS) {
        // We've just finished the last word.
        _uiState.update { currentState ->
            currentState.copy(
                isGuessedWordWrong = false,
                score = updatedScore,
                isGameOver = true
            )
        }
    } else {
        // Move to another word.
        _uiState.update { currentState ->
            currentState.copy(
                isGuessedWordWrong = false,
                currentScrambledWord = pickRandomWordAndShuffle(),
                score = updatedScore,
                currentWordCount = currentState.currentWordCount.inc()
            )
        }
    }
}
```

Import `MAX_NO_OF_WORDS` from `WordsData.kt` if needed.

**Important:** We check the size of `usedWords` *before* selecting the next word. Since the first word was added during initialization, we stop after the player has submitted or skipped the tenth word. No eleventh word is selected.

### 18. Show the end-of-game dialog (slide 31)

The starter already contains `FinalScoreDialog`. Use `isGameOver` to decide when to show it:

```kotlin
if (gameUiState.isGameOver) {
    FinalScoreDialog(
        score = gameUiState.score,
        onPlayAgain = { gameViewModel.resetGame() }
        // Keep the starter's other dialog parameters/callbacks.
    )
}
```

The existing `FinalScoreDialog` uses `AlertDialog` to display the score and provide actions (such as Play Again and Exit). Match its exact parameter names in your starter; the snippet above shows the state-to-dialog connection.

**Checkpoint:** Play 10 words using any mix of correct guesses and skips. The dialog should appear. **Play Again** should reset the score and word count and show a fresh scrambled word.

### 19. Rotate the device (slide 32)

Demonstrate these two actions:

1. Play a few words, changing the score and word count.
2. Rotate the emulator or device to landscape and back.

**What to notice:** A regular `Activity` may be recreated by a configuration change, but the `ViewModel` is retained. The `GameScreen` subscribes to the existing state, so the game continues.

**Useful contrast for students:** A `ViewModel` handles common configuration changes; it is **not** a complete solution for restoring state after the app process is killed.

---

## Part 6 — Unit-test the ViewModel

### 20. Set up the tests (slides 33–34)

Check **`app/build.gradle.kts`** for JUnit 4:

```kotlin
testImplementation("junit:junit:4.13.2")
```

Sync Gradle. Create a local unit-test class (under `src/test`, not `src/androidTest`) called **`GameViewModelTest`**. The slides use the `com.example.unscramble.ui.test` package; adjust the package/imports to match the starter project.

**Explain the test categories:**

- **Success path:** Correct answer increases score and clears error state.
- **Error path:** Incorrect answer leaves score unchanged and sets error state.
- **Boundary cases:** Initial state and completion of the tenth word.

Tests should be **focused, understandable, deterministic, and self-contained**.

### 21. Test a correct guess (slide 35)

The starter's `WordsData.kt` provides `getUnscrambledWord(...)`, which the deck uses to recover the answer from the displayed scrambled word for testing.

```kotlin
private val viewModel = GameViewModel()

@Test
fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
    var currentGameUiState = viewModel.uiState.value
    val correctPlayerWord = getUnscrambledWord(
        currentGameUiState.currentScrambledWord
    )

    viewModel.updateUserGuess(correctPlayerWord)
    viewModel.checkUserGuess()

    currentGameUiState = viewModel.uiState.value
    assertFalse(currentGameUiState.isGuessedWordWrong)
    assertEquals(SCORE_INCREASE, currentGameUiState.score)
}
```

**Ask:** Why do we test `GameViewModel` directly rather than having the test click a button in the emulator?

### 22. Test an incorrect guess (slide 36)

```kotlin
@Test
fun gameViewModel_IncorrectWordGuessed_ErrorFlagSet() {
    val scoreBeforeGuess = viewModel.uiState.value.score

    viewModel.updateUserGuess("and") // Not in the provided word list.
    viewModel.checkUserGuess()

    val stateAfterGuess = viewModel.uiState.value
    assertEquals(scoreBeforeGuess, stateAfterGuess.score)
    assertTrue(stateAfterGuess.isGuessedWordWrong)
}
```

**Checkpoint:** A wrong guess cannot accidentally award points.

### 23. Test the initial state (slides 37–38)

```kotlin
@Test
fun gameViewModel_Initialization_FirstWordLoaded() {
    val state = viewModel.uiState.value
    val answer = getUnscrambledWord(state.currentScrambledWord)

    assertNotEquals(answer, state.currentScrambledWord)
    assertEquals(1, state.currentWordCount)
    assertEquals(0, state.score)
    assertFalse(state.isGuessedWordWrong)
    assertFalse(state.isGameOver)
}
```

**Explain:** Initial state is a boundary case. The first word count is **1**, even before the player answers.

### 24. Test all 10 words (slides 37, 39)

```kotlin
@Test
fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
    var expectedScore = 0
    var state = viewModel.uiState.value
    var answer = getUnscrambledWord(state.currentScrambledWord)

    repeat(MAX_NO_OF_WORDS) {
        expectedScore += SCORE_INCREASE
        viewModel.updateUserGuess(answer)
        viewModel.checkUserGuess()

        state = viewModel.uiState.value
        answer = getUnscrambledWord(state.currentScrambledWord)
        assertEquals(expectedScore, state.score)
    }

    assertEquals(MAX_NO_OF_WORDS, state.currentWordCount)
    assertTrue(state.isGameOver)
}
```

**Explain:** This test drives the actual game logic across the end-of-game boundary without rendering the Compose UI.

**Test imports:** `org.junit.Test` and the relevant functions from `org.junit.Assert` (`assertEquals`, `assertFalse`, `assertTrue`, `assertNotEquals`). Also import the data helpers/constants and `GameViewModel` from their real packages.

### 25. Check coverage (slide 40)

In Android Studio, right-click the test class and select **Run 'GameViewModelTest' with Coverage**.

Use the report to ask:

- Which `ViewModel` methods were exercised?
- Did we test both branches of `checkUserGuess()`?
- Did we test both branches of `updateGameState()`?
- What's missing? (For example, `skipWord()` and `resetGame()` could have their own tests.)

**Emphasis:** High coverage is useful feedback, but it does not automatically mean the tests are good.

---

## End-of-class checklist

- [ ] A `GameViewModel` owns the game logic and exposes `uiState`.
- [ ] `GameScreen` observes `uiState` with `collectAsState()`.
- [ ] The shuffled word comes from the `ViewModel` rather than a hard-coded string.
- [ ] Typing, Submit, Done, and Skip send events back to the `ViewModel`.
- [ ] An incorrect guess shows an error without adding points.
- [ ] A correct guess updates the score and progresses the word count.
- [ ] Skip advances the game without increasing the score.
- [ ] The game ends after 10 words, and Play Again starts over.
- [ ] The score and word count survive device rotation.
- [ ] The `ViewModel` unit tests pass.

## One-minute recap

**Architecture:** The UI displays state; the `ViewModel` handles events and updates state.  
**StateFlow:** A state holder emits a new value, and Compose observes it.  
**Immutability:** We use `copy(...)` to construct a new UI state instead of directly modifying fields.  
**Unidirectional data flow:** **Events go up; state comes down.**  
**Testing:** Since most rules live in the `ViewModel`, we can verify them with ordinary unit tests.

### Reference links from the slides

- [Android app architecture](https://developer.android.com/topic/architecture)
- [Android Basics with Compose — ViewModel and architecture pathway](https://developer.android.com/courses/pathways/android-basics-compose-unit-4-pathway-1)

### Presenter notes / small adjustments from the slide screenshots

The code snippets here preserve the deck's overall implementation and method names. A few examples are intentionally partial because the starter project already contains layout and resource code. In particular, keep the actual composable parameters, styling, string resources, and dialog's Exit callback from your starter project. The dependency versions shown are those in the slides, not a recommendation to override versions already managed by the starter project.
