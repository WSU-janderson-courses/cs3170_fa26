package com.example.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameViewModelTest {
    private val viewModel = GameViewModel()

    @Test
    //  thingUnderTest_TriggerOfTest_ResultOfTest
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset(){
        var currentGameUiState = viewModel.uiState.value
        // from WordsData.kt
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        // assert that checkUserGuess() method correctly updates isGuessedWordWrong
        assertFalse(currentGameUiState.isGuessedWordWrong)

        // assert that the score is updated correctly
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }

    @Test
    fun gameViewModel_IncorrectWordGuessed_ErrorFlagSet(){
        // given as incorrect word as input
        val incorrectPlayerWord = "and" // not in WordData.kt
        var currentGameUiState = viewModel.uiState.value
        val scoreBeforeGuess = currentGameUiState.score

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        val scoreAfterGuess = currentGameUiState.score

        // assert score is unchanged
        assertEquals(scoreBeforeGuess, scoreAfterGuess)
        // assert word guessed wrong is true
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        val gameUiState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        // assert the current word is scrambled
        assertNotEquals(unscrambledWord, gameUiState.currentScrambledWord)
        // assert word count is 1
        assertEquals(1, gameUiState.currentWordCount)
        // assert score is zero
        assertEquals(0, gameUiState.score)
        // assert isGuessedWordWrong is false
        assertFalse(gameUiState.isGameOver)
        // assert isGameOver is false
        assertFalse(gameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        repeat (MAX_NO_OF_WORDS) {
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            // assert after each correct answer, score is updated correctly
            assertEquals(expectedScore, currentGameUiState.score)
        }
        // assert that after all words guessed, current word count is up to date
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        // assert that after all words guessed, isGameOver is true
        assertTrue(currentGameUiState.isGameOver)
    }
}