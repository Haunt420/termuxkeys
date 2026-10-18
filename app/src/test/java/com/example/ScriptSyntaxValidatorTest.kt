package com.example

import com.example.engine.ScriptSyntaxValidator
import com.example.engine.ValidationSeverity
import com.example.model.CustomShortcutDraft
import com.example.model.ScriptType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScriptSyntaxValidatorTest {

    // ==========================================
    // BASH VALIDATION TESTS
    // ==========================================

    @Test
    fun `valid bash function passes syntax validation`() {
        val result = ScriptSyntaxValidator.validateBashFunction(
            name = "deploy_app",
            body = """
                echo "Starting deploy..."
                if [ -d "/data/data/com.termux/files/home" ]; then
                    git pull origin main
                    pkg update -y
                fi
                echo "Deploy finished."
            """.trimIndent()
        )
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `bash function with empty name fails with error`() {
        val result = ScriptSyntaxValidator.validateBashFunction("", "echo 'hello'")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("cannot be blank") })
    }

    @Test
    fun `bash function with invalid characters in identifier fails`() {
        val result = ScriptSyntaxValidator.validateBashFunction("my function!", "echo 1")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("Invalid function name") })
    }

    @Test
    fun `bash function with unbalanced single quotes fails`() {
        val result = ScriptSyntaxValidator.validateBashFunction("test_fn", "echo 'hello world")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("single quote") })
    }

    @Test
    fun `bash function with unbalanced double quotes fails`() {
        val result = ScriptSyntaxValidator.validateBashFunction("test_fn", "echo \"hello world")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("double quote") })
    }

    @Test
    fun `bash function with unbalanced curly braces fails`() {
        val result = ScriptSyntaxValidator.validateBashFunction("test_fn", "foo() { echo 1; ")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("curly brace") })
    }

    @Test
    fun `bash function with unbalanced parentheses fails`() {
        val result = ScriptSyntaxValidator.validateBashFunction("test_fn", "res=(ls -la")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("parenthesis") })
    }

    @Test
    fun `bash variable assignment with space before equals fails with helpful tip`() {
        val result = ScriptSyntaxValidator.validateBashFunction("test_fn", "COUNT = 10")
        assertFalse(result.isValid)
        val error = result.errors.find { it.message.contains("space before '='") }
        assertTrue(error != null)
        assertTrue(error?.suggestion?.contains("without spaces") == true || error?.suggestion?.contains("COUNT=value") == true)
    }

    @Test
    fun `bash variable assignment with space after equals fails with helpful tip`() {
        val result = ScriptSyntaxValidator.validateBashFunction("test_fn", "COUNT= 10")
        assertFalse(result.isValid)
        val error = result.errors.find { it.message.contains("space after '='") }
        assertTrue(error != null)
    }

    @Test
    fun `bash missing fi for if statement fails`() {
        val result = ScriptSyntaxValidator.validateBashFunction(
            name = "test_if",
            body = "if [ -f file.txt ]; then echo 'found'; "
        )
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("Missing 'fi'") })
    }

    @Test
    fun `bash missing done for for loop fails`() {
        val result = ScriptSyntaxValidator.validateBashFunction(
            name = "test_loop",
            body = "for x in 1 2 3; do echo \$x; "
        )
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("Missing 'done'") })
    }

    @Test
    fun `bash test bracket missing space generates warning tip`() {
        val result = ScriptSyntaxValidator.validateBashFunction("test_bracket", "if [\$x == 1 ]; then echo ok; fi")
        assertTrue(result.warnings.any { it.message.contains("Missing space after opening bracket") })
    }

    // ==========================================
    // PYTHON VALIDATION TESTS
    // ==========================================

    @Test
    fun `valid python script passes validation`() {
        val pyCode = """
            #!/usr/bin/env python3
            import sys
            import json

            def main():
                data = {"status": "ok", "count": 42}
                print(json.dumps(data, indent=2))

            if __name__ == "__main__":
                main()
        """.trimIndent()
        val result = ScriptSyntaxValidator.validatePythonScript(pyCode)
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `python missing colon in def statement fails`() {
        val pyCode = """
            def greet(name)
                print(f"Hello {name}")
        """.trimIndent()
        val result = ScriptSyntaxValidator.validatePythonScript(pyCode)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("Missing colon ':' at end of 'def'") })
    }

    @Test
    fun `python missing colon in if statement fails`() {
        val pyCode = """
            if count > 0
                print("Positive")
        """.trimIndent()
        val result = ScriptSyntaxValidator.validatePythonScript(pyCode)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("Missing colon ':' at end of 'if'") })
    }

    @Test
    fun `python assignment in if statement is caught`() {
        val pyCode = """
            if count = 5:
                print("Five")
        """.trimIndent()
        val result = ScriptSyntaxValidator.validatePythonScript(pyCode)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("Assignment '=' used inside conditional") })
    }

    @Test
    fun `python using shell logical AND is caught`() {
        val pyCode = """
            if a > 0 && b > 0:
                print("Both positive")
        """.trimIndent()
        val result = ScriptSyntaxValidator.validatePythonScript(pyCode)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("Logical operator '&&'") })
    }

    @Test
    fun `python lowercase boolean emits warning`() {
        val pyCode = """
            active = true
        """.trimIndent()
        val result = ScriptSyntaxValidator.validatePythonScript(pyCode)
        assertTrue(result.warnings.any { it.message.contains("Non-capitalized keyword 'true'") })
    }

    @Test
    fun `python unclosed brackets fails`() {
        val pyCode = """
            items = [1, 2, 3
            print(items)
        """.trimIndent()
        val result = ScriptSyntaxValidator.validatePythonScript(pyCode)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("Unclosed '['") })
    }

    @Test
    fun `python unclosed string literal fails`() {
        val pyCode = """
            msg = "Hello World
            print(msg)
        """.trimIndent()
        val result = ScriptSyntaxValidator.validatePythonScript(pyCode)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("Unterminated string literal") })
    }

    @Test
    fun `python unclosed triple quotes fails`() {
        val quotes = "\"\"\""
        val pyCode = "def doc():\n    $quotes This is an unclosed docstring.\n    print(\"test\")"
        val result = ScriptSyntaxValidator.validatePythonScript(pyCode)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("Unclosed triple-quoted string") })
    }

    // ==========================================
    // SHORTCUT DRAFT VALIDATION TESTS
    // ==========================================

    @Test
    fun `valid shortcut draft passes validation`() {
        val draft = CustomShortcutDraft(
            name = "sysup",
            type = ScriptType.BASH_FUNCTION,
            commandOrBody = "pkg update -y && pkg upgrade -y",
            keyDisplay = "SYS-UP",
            popupDisplay = "CLEAN",
            popupCode = "pkg clean\n"
        )
        val result = ScriptSyntaxValidator.validateShortcutDraft(draft)
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `draft with empty key label fails`() {
        val draft = CustomShortcutDraft(
            name = "mycmd",
            type = ScriptType.BASH_FUNCTION,
            commandOrBody = "echo 1",
            keyDisplay = ""
        )
        val result = ScriptSyntaxValidator.validateShortcutDraft(draft)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.message.contains("display label cannot be empty") })
    }

    @Test
    fun `draft with overly long key label warns about screen overflow`() {
        val draft = CustomShortcutDraft(
            name = "longlabel",
            type = ScriptType.BASH_FUNCTION,
            commandOrBody = "echo 1",
            keyDisplay = "VERY_LONG_BUTTON"
        )
        val result = ScriptSyntaxValidator.validateShortcutDraft(draft)
        assertTrue(result.isValid) // Warning does not block validity
        assertTrue(result.warnings.any { it.message.contains("characters long") })
    }
}
