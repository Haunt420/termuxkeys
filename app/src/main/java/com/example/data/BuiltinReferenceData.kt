package com.example.data

import com.example.model.ReferenceSection
import com.example.model.ReferenceTopic

object BuiltinReferenceData {

    val topics: List<ReferenceTopic> = listOf(
        // ─── Topic 1: Termux Extra Keys Syntax ────────────────────────────────────────
        ReferenceTopic(
            id = "ref_termux_syntax",
            title = "Termux Extra Keys Syntax Guide",
            category = "Termux Configuration",
            summary = "Complete specification of extra-keys, extra-keys-style, popups, and formatting in ~/.termux/termux.properties.",
            relatedKeys = listOf("ESC", "CTRL", "ALT", "TAB", "DRAWER", "KEYBOARD", "PASTE"),
            sections = listOf(
                ReferenceSection(
                    heading = "1. Configuration File Location",
                    content = "Termux reads its interface preferences from the file '~/.termux/termux.properties'. If the '.termux' folder or file does not exist, you can create it with 'mkdir -p ~/.termux'. Changes take effect immediately once you execute 'termux-reload-settings' in any active terminal session.",
                    codeSnippet = "mkdir -p ~/.termux\nnano ~/.termux/termux.properties\ntermux-reload-settings"
                ),
                ReferenceSection(
                    heading = "2. The 'extra-keys' Matrix Grammar",
                    content = "The extra-keys property is a two-dimensional array of rows and columns. Each outer bracket represents the keyboard bar, each inner bracket represents a row of buttons, and each element inside is a key identifier string or configuration dictionary. Termux supports 1 to 4 rows.",
                    codeSnippet = "extra-keys = [ \\\n  ['ESC', 'TAB', 'CTRL', 'ALT', 'UP', 'DOWN'], \\\n  ['HOME', 'END', 'PGUP', 'PGDN', '|', 'ENTER'] \\\n]"
                ),
                ReferenceSection(
                    heading = "3. Custom Display Labels & Symbols",
                    content = "If a key requires a friendly or compact label different from its emitted character or keycode, define it as an object with 'key' and 'display' fields:",
                    codeSnippet = "{key: 'ESC', display: '⎋'}\n{key: 'ENTER', display: '↵'}\n{key: 'BACKSLASH', display: '\\\\'}"
                ),
                ReferenceSection(
                    heading = "4. Swipe-Up Popup Keys",
                    content = "Termux supports swipe-up gesture popups on any key. When swiping up from a button, the popup key or macro is dispatched instead of the primary key. You can specify a simple string or a nested macro object:",
                    codeSnippet = "# Simple popup:\n{key: 'CTRL', popup: 'ALT'}\n\n# Popup with macro text and custom label:\n{key: '|', popup: {macro: ' | grep ', display: 'grep'}}"
                ),
                ReferenceSection(
                    heading = "5. Extra Keys Styling Options",
                    content = "Termux supports two visual styles for the extra keys bar via 'extra-keys-style':\n• 'default': Renders keys with full text labels and standard button styling.\n• 'arrows-only': Compact arrow navigation layout.",
                    codeSnippet = "extra-keys-style = default\n# or\nextra-keys-style = arrows-only"
                )
            )
        ),

        // ─── Topic 2: GNU Nano Complete Reference ─────────────────────────────────────
        ReferenceTopic(
            id = "ref_nano_guide",
            title = "GNU Nano Complete Reference Manual",
            category = "Terminal Editors",
            summary = "Exhaustive shortcut handbook for GNU Nano: saving, navigation, text cutting, search, replace, and editor options.",
            relatedKeys = listOf("^O", "^X", "^W", "^K", "^U", "^J", "^C", "^G", "^\\", "M-U", "M-E", "M-A"),
            sections = listOf(
                ReferenceSection(
                    heading = "1. File Operations & Buffer Control",
                    content = "• Ctrl+O (^O) - WriteOut: Saves the current buffer to disk. You can press Enter to overwrite or change the filename.\n• Ctrl+X (^X) - Exit: Closes the editor. If changes are unsaved, it prompts (Y/N/Ctrl+C).\n• Ctrl+R (^R) - Read File: Inserts an external file into the current buffer at cursor position.",
                    codeSnippet = "^O  WriteOut (Save file)\n^X  Exit Nano\n^R  Read/Insert external file"
                ),
                ReferenceSection(
                    heading = "2. Search & Text Replacement",
                    content = "• Ctrl+W (^W) - Where Is: Interactive search. Type keyword and press Enter. Press Alt+W to repeat search forward.\n• Ctrl+\\ (^\\) - Replace: Search and replace string. Prompts for target and replacement text, then offers (Y)es, (N)o, (A)ll.",
                    codeSnippet = "^W  Search for string\n^\\  Search & Replace\nM-W Repeat last search"
                ),
                ReferenceSection(
                    heading = "3. Cutting, Pasting & Cutbuffer",
                    content = "• Ctrl+K (^K) - Cut: Cuts the current line into the cutbuffer. Multiple consecutive ^K calls aggregate lines into a single cutbuffer block.\n• Ctrl+U (^U) - UnCut: Pastes cutbuffer contents at current cursor location.\n• Alt+A (M-A) - Mark Text: Begin selecting a visual block. Use arrow keys to select, then ^K to cut or Alt+6 (M-6) to copy.",
                    codeSnippet = "^K  Cut line / selection\n^U  Paste (UnCut)\nM-A Start visual text selection\nM-6 Copy marked text"
                ),
                ReferenceSection(
                    heading = "4. Undo, Redo & Cursor Info",
                    content = "• Alt+U (M-U) - Undo: Revert the last typing or modification action.\n• Alt+E (M-E) - Redo: Reapply undone modifications.\n• Ctrl+C (^C) - Cur Pos: Shows current line number, total lines, column number, and character offset.",
                    codeSnippet = "M-U Undo edit\nM-E Redo edit\n^C  Show cursor position"
                )
            )
        ),

        // ─── Topic 3: Bash & Zsh Readline Cheatsheet ──────────────────────────────────
        ReferenceTopic(
            id = "ref_readline_bash",
            title = "Bash & Zsh Readline Hotkeys",
            category = "Shell & CLI",
            summary = "Master command line editing: line jumping, reverse history search, word deletions, and job control signals.",
            relatedKeys = listOf("^C", "^D", "^Z", "^L", "^R", "^A", "^E", "^U", "^K", "^W"),
            sections = listOf(
                ReferenceSection(
                    heading = "1. Cursor Movement on the Command Line",
                    content = "• Ctrl+A: Jump directly to the beginning of the command line.\n• Ctrl+E: Jump directly to the end of the command line.\n• Alt+F: Move forward one word.\n• Alt+B: Move backward one word.",
                    codeSnippet = "Ctrl+A  Start of line\nCtrl+E  End of line\nAlt+F   Forward one word\nAlt+B   Backward one word"
                ),
                ReferenceSection(
                    heading = "2. Quick Text Deletion & Kill Ring",
                    content = "• Ctrl+U: Erase/cut everything from the cursor back to the start of the line.\n• Ctrl+K: Erase/cut everything from the cursor forward to the end of the line.\n• Ctrl+W: Delete the word immediately before the cursor.\n• Ctrl+Y: Yank (paste) the last killed text back into the line.",
                    codeSnippet = "Ctrl+U  Cut to line start\nCtrl+K  Cut to line end\nCtrl+W  Delete previous word\nCtrl+Y  Yank / Paste text"
                ),
                ReferenceSection(
                    heading = "3. History & Recall Shortcuts",
                    content = "• Ctrl+R: Reverse incremental search through command history. Type letters to match; press Ctrl+R repeatedly to cycle older matches.\n• !! : Rerun previous command (e.g. 'sudo !!').\n• !$: Insert the last argument of the previous command.",
                    codeSnippet = "Ctrl+R  Reverse history search\nsudo !! Run last command as root\n!$      Last argument of previous command"
                ),
                ReferenceSection(
                    heading = "4. Process & Signal Management",
                    content = "• Ctrl+C (SIGINT): Interrupts and halts the active foreground process immediately.\n• Ctrl+D (EOF): Closes standard input or terminates the shell session.\n• Ctrl+Z (SIGTSTP): Pauses process and sends it to the background. Resume with 'fg'.\n• Ctrl+L: Redraws and clears the visible screen buffer.",
                    codeSnippet = "Ctrl+C  SIGINT (Interrupt process)\nCtrl+D  EOF (Exit shell)\nCtrl+Z  Suspend to background\nfg      Resume suspended job"
                )
            )
        ),

        // ─── Topic 4: Unix Piping & Redirection ───────────────────────────────────────
        ReferenceTopic(
            id = "ref_piping_redirection",
            title = "Unix I/O Redirection & Pipeline Masterclass",
            category = "Unix Architecture",
            summary = "Understanding file descriptors 0, 1, 2, stdout/stderr redirection, pipeline chaining, and filter commands.",
            relatedKeys = listOf("|", ">", ">>", "<", "2>&1", "&&", "||", ";", "&"),
            sections = listOf(
                ReferenceSection(
                    heading = "1. The Three Standard File Descriptors",
                    content = "Every Unix process launched in Termux starts with three file descriptors:\n• 0: Standard Input (stdin) - keyboard/input stream\n• 1: Standard Output (stdout) - normal terminal output\n• 2: Standard Error (stderr) - diagnostic and error messages",
                    codeSnippet = "0 = stdin\n1 = stdout\n2 = stderr"
                ),
                ReferenceSection(
                    heading = "2. Piping with '|'",
                    content = "The pipe symbol connects stdout of the preceding command to stdin of the subsequent command. This allows building powerful data processing pipelines without temporary files.",
                    codeSnippet = "# Count lines of error in system logs:\ncat log.txt | grep 'ERROR' | wc -l"
                ),
                ReferenceSection(
                    heading = "3. File Redirection Operators",
                    content = "• '>' Overwrite: Directs stdout to a file, replacing existing content.\n• '>>' Append: Appends stdout to the file without deleting existing lines.\n• '<' Input: Feeds a file into command standard input.\n• '2>&1' Merge: Redirects stderr (2) into stdout (1) so errors can be piped or captured in logs.",
                    codeSnippet = "# Redirect both stdout and stderr to a log file:\npython script.py > output.log 2>&1\n\n# Discard error output:\nfind / -name 'foo' 2> /dev/null"
                ),
                ReferenceSection(
                    heading = "4. Conditional Execution Operators",
                    content = "• '&&' (AND): Executes next command only if prior command exits with 0 (success).\n• '||' (OR): Executes next command only if prior command fails (non-zero exit).\n• ';' (Sequence): Runs commands sequentially in order.",
                    codeSnippet = "pkg update -y && pkg upgrade -y\nping -c 1 8.8.8.8 || echo 'Network offline'"
                )
            )
        ),

        // ─── Topic 5: Termux System Tips & SAF ────────────────────────────────────────
        ReferenceTopic(
            id = "ref_termux_tips",
            title = "Termux Tips & Storage Integration",
            category = "Termux Tips",
            summary = "Practical guide to Termux file access, Storage Access Framework (SAF), and session controls.",
            relatedKeys = listOf("DRAWER", "KEYBOARD", "PASTE"),
            sections = listOf(
                ReferenceSection(
                    heading = "1. Storage Access Framework (SAF)",
                    content = "Termux provides a DocumentProvider allowing external Android apps with SAF to read and write to Termux files. You can grant access to Termux home or export files to Android shared storage (/sdcard/Download) and copy them into '~/.termux'.",
                    codeSnippet = "# Enable Termux shared storage access:\ntermux-setup-storage\n\n# Copy file from Download to .termux:\ncp /sdcard/Download/termux.properties ~/.termux/"
                ),
                ReferenceSection(
                    heading = "2. Special Termux Action Keys",
                    content = "Termux provides built-in action strings that interact directly with the Android host interface:\n• DRAWER: Opens the side drawer menu showing active sessions and failsafe option.\n• KEYBOARD: Toggles software keyboard on/off.\n• PASTE: Emits Android clipboard text.",
                    codeSnippet = "extra-keys = [['DRAWER', 'KEYBOARD', 'PASTE']]"
                )
            )
        )
    )
}
