package com.example.data

import com.example.model.CatalogCategory
import com.example.model.CatalogItem

object BuiltinCatalogData {

    val items: List<CatalogItem> = listOf(
        // ─── 1. Modifiers & Core System Keys ───────────────────────────────────────────
        CatalogItem(
            id = "mod_ctrl",
            name = "Control Modifier (CTRL)",
            keyString = "CTRL",
            category = CatalogCategory.MODIFIERS,
            description = "Hold or toggle to send control sequences (e.g., Ctrl+C, Ctrl+D).",
            defaultDisplay = "CTRL",
            searchTags = listOf("modifier", "control", "ctrl", "^")
        ),
        CatalogItem(
            id = "mod_alt",
            name = "Alt / Meta Modifier (ALT)",
            keyString = "ALT",
            category = CatalogCategory.MODIFIERS,
            description = "Hold or toggle to send meta / escape key combinations.",
            defaultDisplay = "ALT",
            searchTags = listOf("modifier", "alt", "meta", "option")
        ),
        CatalogItem(
            id = "mod_esc",
            name = "Escape Key (ESC)",
            keyString = "ESC",
            category = CatalogCategory.MODIFIERS,
            description = "Escape sequence key. Used in Vim normal mode, canceling prompts, and exiting dialogues.",
            defaultDisplay = "ESC",
            searchTags = listOf("escape", "esc", "cancel")
        ),
        CatalogItem(
            id = "mod_tab",
            name = "Tabulator (TAB)",
            keyString = "TAB",
            category = CatalogCategory.MODIFIERS,
            description = "Tab key for command and filename autocompletion in bash/zsh and code indentation.",
            defaultDisplay = "TAB",
            searchTags = listOf("tab", "autocomplete", "indent", "completion")
        ),
        CatalogItem(
            id = "mod_enter",
            name = "Enter / Return",
            keyString = "ENTER",
            category = CatalogCategory.MODIFIERS,
            description = "Execute command or insert newline.",
            defaultDisplay = "↵",
            searchTags = listOf("enter", "return", "newline", "execute")
        ),
        CatalogItem(
            id = "mod_bksp",
            name = "Backspace (BKSP)",
            keyString = "BKSP",
            category = CatalogCategory.MODIFIERS,
            description = "Erase the character before the cursor.",
            defaultDisplay = "⌫",
            searchTags = listOf("backspace", "delete", "erase")
        ),
        CatalogItem(
            id = "mod_del",
            name = "Delete (DEL)",
            keyString = "DEL",
            category = CatalogCategory.MODIFIERS,
            description = "Erase the character under or immediately after the cursor.",
            defaultDisplay = "DEL",
            searchTags = listOf("del", "delete", "forward delete")
        ),
        CatalogItem(
            id = "mod_ins",
            name = "Insert (INS)",
            keyString = "INS",
            category = CatalogCategory.MODIFIERS,
            description = "Toggle insert / overwrite mode in terminal text editors.",
            defaultDisplay = "INS",
            searchTags = listOf("insert", "ins", "overwrite")
        ),

        // ─── 2. Navigation & Directional ──────────────────────────────────────────────
        CatalogItem(
            id = "nav_up",
            name = "Up Arrow (UP)",
            keyString = "UP",
            category = CatalogCategory.NAVIGATION,
            description = "Navigate cursor up or recall previous command from shell history.",
            defaultDisplay = "▲",
            searchTags = listOf("up", "arrow", "history", "previous")
        ),
        CatalogItem(
            id = "nav_down",
            name = "Down Arrow (DOWN)",
            keyString = "DOWN",
            category = CatalogCategory.NAVIGATION,
            description = "Navigate cursor down or advance through command history.",
            defaultDisplay = "▼",
            searchTags = listOf("down", "arrow", "history", "next")
        ),
        CatalogItem(
            id = "nav_left",
            name = "Left Arrow (LEFT)",
            keyString = "LEFT",
            category = CatalogCategory.NAVIGATION,
            description = "Move cursor one character to the left.",
            defaultDisplay = "◀",
            searchTags = listOf("left", "arrow", "back")
        ),
        CatalogItem(
            id = "nav_right",
            name = "Right Arrow (RIGHT)",
            keyString = "RIGHT",
            category = CatalogCategory.NAVIGATION,
            description = "Move cursor one character to the right.",
            defaultDisplay = "▶",
            searchTags = listOf("right", "arrow", "forward")
        ),
        CatalogItem(
            id = "nav_home",
            name = "Home",
            keyString = "HOME",
            category = CatalogCategory.NAVIGATION,
            description = "Jump cursor to the start of the current line or file.",
            defaultDisplay = "HOME",
            searchTags = listOf("home", "beginning", "start")
        ),
        CatalogItem(
            id = "nav_end",
            name = "End",
            keyString = "END",
            category = CatalogCategory.NAVIGATION,
            description = "Jump cursor to the end of the current line or file.",
            defaultDisplay = "END",
            searchTags = listOf("end", "finish", "tail")
        ),
        CatalogItem(
            id = "nav_pgup",
            name = "Page Up (PGUP)",
            keyString = "PGUP",
            category = CatalogCategory.NAVIGATION,
            description = "Scroll up one full terminal screen or page buffer.",
            defaultDisplay = "PGUP",
            searchTags = listOf("page up", "pgup", "scroll up")
        ),
        CatalogItem(
            id = "nav_pgdn",
            name = "Page Down (PGDN)",
            keyString = "PGDN",
            category = CatalogCategory.NAVIGATION,
            description = "Scroll down one full terminal screen or page buffer.",
            defaultDisplay = "PGDN",
            searchTags = listOf("page down", "pgdn", "scroll down")
        ),

        // ─── 3. Function Keys (F1-F12) ────────────────────────────────────────────────
        CatalogItem(
            id = "fn_f1",
            name = "Function Key F1",
            keyString = "F1",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F1 key (Help in Midnight Commander, Htop help, etc.).",
            defaultDisplay = "F1",
            searchTags = listOf("f1", "function", "help")
        ),
        CatalogItem(
            id = "fn_f2",
            name = "Function Key F2",
            keyString = "F2",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F2 key (Setup in Htop, Rename/User menu in MC).",
            defaultDisplay = "F2",
            searchTags = listOf("f2", "function", "setup")
        ),
        CatalogItem(
            id = "fn_f3",
            name = "Function Key F3",
            keyString = "F3",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F3 key (View in Midnight Commander, Search in Htop).",
            defaultDisplay = "F3",
            searchTags = listOf("f3", "function", "view")
        ),
        CatalogItem(
            id = "fn_f4",
            name = "Function Key F4",
            keyString = "F4",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F4 key (Edit in Midnight Commander, Filter in Htop).",
            defaultDisplay = "F4",
            searchTags = listOf("f4", "function", "edit", "filter")
        ),
        CatalogItem(
            id = "fn_f5",
            name = "Function Key F5",
            keyString = "F5",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F5 key (Copy in MC, Tree view in Htop, Refresh).",
            defaultDisplay = "F5",
            searchTags = listOf("f5", "function", "copy", "tree")
        ),
        CatalogItem(
            id = "fn_f6",
            name = "Function Key F6",
            keyString = "F6",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F6 key (Move/Rename in MC, Sort by in Htop).",
            defaultDisplay = "F6",
            searchTags = listOf("f6", "function", "move", "sort")
        ),
        CatalogItem(
            id = "fn_f7",
            name = "Function Key F7",
            keyString = "F7",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F7 key (Mkdir in MC, Nice value - in Htop).",
            defaultDisplay = "F7",
            searchTags = listOf("f7", "function", "mkdir", "nice")
        ),
        CatalogItem(
            id = "fn_f8",
            name = "Function Key F8",
            keyString = "F8",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F8 key (Delete in MC, Nice value + in Htop).",
            defaultDisplay = "F8",
            searchTags = listOf("f8", "function", "delete", "nice")
        ),
        CatalogItem(
            id = "fn_f9",
            name = "Function Key F9",
            keyString = "F9",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F9 key (Pull-down menu in MC, Kill process in Htop).",
            defaultDisplay = "F9",
            searchTags = listOf("f9", "function", "kill", "menu")
        ),
        CatalogItem(
            id = "fn_f10",
            name = "Function Key F10",
            keyString = "F10",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F10 key (Quit in MC and Htop).",
            defaultDisplay = "F10",
            searchTags = listOf("f10", "function", "quit", "exit")
        ),
        CatalogItem(
            id = "fn_f11",
            name = "Function Key F11",
            keyString = "F11",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F11 key (Fullscreen toggle in terminals).",
            defaultDisplay = "F11",
            searchTags = listOf("f11", "function", "fullscreen")
        ),
        CatalogItem(
            id = "fn_f12",
            name = "Function Key F12",
            keyString = "F12",
            category = CatalogCategory.FUNCTION_KEYS,
            description = "F12 key (Save as or developer tools trigger).",
            defaultDisplay = "F12",
            searchTags = listOf("f12", "function")
        ),

        // ─── 4. Termux Actions ────────────────────────────────────────────────────────
        CatalogItem(
            id = "termux_drawer",
            name = "Toggle Termux Drawer (DRAWER)",
            keyString = "DRAWER",
            category = CatalogCategory.TERMUX_ACTIONS,
            description = "Open or close the Termux navigation drawer sidebar (session list).",
            defaultDisplay = "☰",
            searchTags = listOf("drawer", "sidebar", "sessions", "termux")
        ),
        CatalogItem(
            id = "termux_keyboard",
            name = "Toggle Keyboard (KEYBOARD)",
            keyString = "KEYBOARD",
            category = CatalogCategory.TERMUX_ACTIONS,
            description = "Toggle the visibility of the Android virtual keyboard.",
            defaultDisplay = "⌨",
            searchTags = listOf("keyboard", "ime", "soft keyboard", "termux")
        ),
        CatalogItem(
            id = "termux_paste",
            name = "Paste Clipboard (PASTE)",
            keyString = "PASTE",
            category = CatalogCategory.TERMUX_ACTIONS,
            description = "Paste Android system clipboard contents directly into the current terminal session.",
            defaultDisplay = "📋",
            searchTags = listOf("paste", "clipboard", "insert")
        ),

        // ─── 5. Control Signals & Sequences ───────────────────────────────────────────
        CatalogItem(
            id = "sig_int",
            name = "Interrupt Signal (Ctrl+C)",
            keyString = "CTRL",
            category = CatalogCategory.CONTROL_SIGNALS,
            description = "Send SIGINT to terminate or interrupt the currently running foreground process.",
            defaultDisplay = "^C",
            suggestedPopup = "SIGINT",
            searchTags = listOf("ctrl+c", "interrupt", "sigint", "cancel", "kill")
        ),
        CatalogItem(
            id = "sig_eof",
            name = "End of Transmission / File (Ctrl+D)",
            keyString = "CTRL",
            category = CatalogCategory.CONTROL_SIGNALS,
            description = "Send EOF. Exits the current shell session or closes input stream.",
            defaultDisplay = "^D",
            searchTags = listOf("ctrl+d", "eof", "logout", "exit")
        ),
        CatalogItem(
            id = "sig_tstp",
            name = "Suspend Process (Ctrl+Z)",
            keyString = "CTRL",
            category = CatalogCategory.CONTROL_SIGNALS,
            description = "Send SIGTSTP to pause and suspend foreground process to background (resume with fg).",
            defaultDisplay = "^Z",
            searchTags = listOf("ctrl+z", "suspend", "pause", "sigtstp", "background")
        ),
        CatalogItem(
            id = "sig_clear",
            name = "Clear Screen (Ctrl+L)",
            keyString = "CTRL",
            category = CatalogCategory.CONTROL_SIGNALS,
            description = "Clear current terminal display without losing active shell command buffer.",
            defaultDisplay = "^L",
            searchTags = listOf("ctrl+l", "clear", "cls", "redraw")
        ),
        CatalogItem(
            id = "sig_rsearch",
            name = "Reverse History Search (Ctrl+R)",
            keyString = "CTRL",
            category = CatalogCategory.CONTROL_SIGNALS,
            description = "Interactively search backward through previously executed shell commands.",
            defaultDisplay = "^R",
            searchTags = listOf("ctrl+r", "search", "history", "reverse search")
        ),
        CatalogItem(
            id = "sig_home_line",
            name = "Beginning of Line (Ctrl+A)",
            keyString = "CTRL",
            category = CatalogCategory.CONTROL_SIGNALS,
            description = "Jump cursor directly to the beginning of the command line in Readline/Bash.",
            defaultDisplay = "^A",
            searchTags = listOf("ctrl+a", "line start", "beginning", "home")
        ),
        CatalogItem(
            id = "sig_end_line",
            name = "End of Line (Ctrl+E)",
            keyString = "CTRL",
            category = CatalogCategory.CONTROL_SIGNALS,
            description = "Jump cursor directly to the end of the command line in Readline/Bash.",
            defaultDisplay = "^E",
            searchTags = listOf("ctrl+e", "line end", "end")
        ),
        CatalogItem(
            id = "sig_kill_to_start",
            name = "Erase to Start of Line (Ctrl+U)",
            keyString = "CTRL",
            category = CatalogCategory.CONTROL_SIGNALS,
            description = "Cut/erase all text from the cursor back to the beginning of the line.",
            defaultDisplay = "^U",
            searchTags = listOf("ctrl+u", "kill", "erase", "clear line")
        ),
        CatalogItem(
            id = "sig_kill_to_end",
            name = "Erase to End of Line (Ctrl+K)",
            keyString = "CTRL",
            category = CatalogCategory.CONTROL_SIGNALS,
            description = "Cut/erase all text from the cursor forward to the end of the line.",
            defaultDisplay = "^K",
            searchTags = listOf("ctrl+k", "kill end", "erase")
        ),
        CatalogItem(
            id = "sig_kill_word",
            name = "Erase Word Backward (Ctrl+W)",
            keyString = "CTRL",
            category = CatalogCategory.CONTROL_SIGNALS,
            description = "Erase the word immediately preceding the cursor in bash/zsh.",
            defaultDisplay = "^W",
            searchTags = listOf("ctrl+w", "delete word", "kill word", "backspace word")
        ),

        // ─── 6. GNU Nano Commands ─────────────────────────────────────────────────────
        CatalogItem(
            id = "nano_writeout",
            name = "Nano: WriteOut / Save (^O)",
            keyString = "CTRL",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Save the current file buffer in GNU nano (Ctrl+O).",
            defaultDisplay = "^O",
            suggestedPopup = "WriteOut",
            searchTags = listOf("nano", "save", "writeout", "^o", "write")
        ),
        CatalogItem(
            id = "nano_exit",
            name = "Nano: Exit (^X)",
            keyString = "CTRL",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Exit GNU nano editor. Prompts to save if modifications are unsaved.",
            defaultDisplay = "^X",
            suggestedPopup = "Exit",
            searchTags = listOf("nano", "exit", "quit", "^x", "close")
        ),
        CatalogItem(
            id = "nano_whereis",
            name = "Nano: Where Is / Search (^W)",
            keyString = "CTRL",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Search forward in file buffer for string or regular expression in Nano.",
            defaultDisplay = "^W",
            suggestedPopup = "Search",
            searchTags = listOf("nano", "search", "where is", "find", "^w")
        ),
        CatalogItem(
            id = "nano_replace",
            name = "Nano: Replace (^\\)",
            keyString = "CTRL",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Search for a string and prompt to replace occurrences throughout file.",
            defaultDisplay = "^\\",
            searchTags = listOf("nano", "replace", "substitute", "find replace")
        ),
        CatalogItem(
            id = "nano_cut",
            name = "Nano: Cut Text (^K)",
            keyString = "CTRL",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Cut the current line (or selected marked region) to the cutbuffer.",
            defaultDisplay = "^K",
            suggestedPopup = "Cut",
            searchTags = listOf("nano", "cut", "kill text", "delete line")
        ),
        CatalogItem(
            id = "nano_uncut",
            name = "Nano: UnCut / Paste (^U)",
            keyString = "CTRL",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Paste the contents of the cutbuffer at the current cursor position.",
            defaultDisplay = "^U",
            suggestedPopup = "Paste",
            searchTags = listOf("nano", "uncut", "paste", "insert cut")
        ),
        CatalogItem(
            id = "nano_justify",
            name = "Nano: Justify Paragraph (^J)",
            keyString = "CTRL",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Reformat and wrap the current paragraph to fit standard line width.",
            defaultDisplay = "^J",
            suggestedPopup = "Justify",
            searchTags = listOf("nano", "justify", "wrap text", "paragraph")
        ),
        CatalogItem(
            id = "nano_curpos",
            name = "Nano: Cursor Position (^C)",
            keyString = "CTRL",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Display current line number, column number, and character count in status bar.",
            defaultDisplay = "^C",
            suggestedPopup = "LineInfo",
            searchTags = listOf("nano", "cursor position", "line number", "info")
        ),
        CatalogItem(
            id = "nano_help",
            name = "Nano: Get Help (^G)",
            keyString = "CTRL",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Open the built-in GNU nano comprehensive help viewer.",
            defaultDisplay = "^G",
            suggestedPopup = "Help",
            searchTags = listOf("nano", "help", "manual", "guide")
        ),
        CatalogItem(
            id = "nano_readfile",
            name = "Nano: Read File (^R)",
            keyString = "CTRL",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Insert the contents of another file directly into current buffer.",
            defaultDisplay = "^R",
            suggestedPopup = "ReadFile",
            searchTags = listOf("nano", "read file", "insert file", "include")
        ),
        CatalogItem(
            id = "nano_undo",
            name = "Nano: Undo (Alt+U)",
            keyString = "ALT",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Undo the most recent edit action in Nano buffer.",
            defaultDisplay = "M-U",
            suggestedPopup = "Undo",
            searchTags = listOf("nano", "undo", "alt+u", "revert")
        ),
        CatalogItem(
            id = "nano_redo",
            name = "Nano: Redo (Alt+E)",
            keyString = "ALT",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Redo the previously undone edit action in Nano buffer.",
            defaultDisplay = "M-E",
            suggestedPopup = "Redo",
            searchTags = listOf("nano", "redo", "alt+e")
        ),
        CatalogItem(
            id = "nano_mark",
            name = "Nano: Set Mark / Select (Alt+A)",
            keyString = "ALT",
            category = CatalogCategory.NANO_COMMANDS,
            description = "Toggle visual text selection mode to highlight a region for cut/copy.",
            defaultDisplay = "M-A",
            suggestedPopup = "Mark",
            searchTags = listOf("nano", "mark", "select", "visual mode", "highlight")
        ),

        // ─── 7. Bash & Zsh Power Commands ─────────────────────────────────────────────
        CatalogItem(
            id = "bash_sudo_last",
            name = "Rerun with Sudo (sudo !!)",
            keyString = "sudo !!",
            category = CatalogCategory.BASH_READLINE,
            description = "Execute the preceding command with root / sudo permissions.",
            defaultDisplay = "sudo !!",
            searchTags = listOf("sudo", "bang bang", "root", "permission")
        ),
        CatalogItem(
            id = "bash_last_arg",
            name = "Last Argument (!\$)",
            keyString = "!$",
            category = CatalogCategory.BASH_READLINE,
            description = "Inserts the last argument of the previously executed command.",
            defaultDisplay = "!$",
            searchTags = listOf("last argument", "bang dollar", "arg")
        ),
        CatalogItem(
            id = "bash_cd_home",
            name = "Change to Home (cd ~)",
            keyString = "cd ~",
            category = CatalogCategory.BASH_READLINE,
            description = "Jump immediately to user home directory (`~`).",
            defaultDisplay = "cd ~",
            searchTags = listOf("cd", "home", "directory")
        ),
        CatalogItem(
            id = "bash_cd_prev",
            name = "Previous Directory (cd -)",
            keyString = "cd -",
            category = CatalogCategory.BASH_READLINE,
            description = "Switch back to the previous working directory.",
            defaultDisplay = "cd -",
            searchTags = listOf("cd -", "previous directory", "back")
        ),
        CatalogItem(
            id = "bash_cd_up",
            name = "Parent Directory (cd ..)",
            keyString = "cd ..",
            category = CatalogCategory.BASH_READLINE,
            description = "Step up one directory level into parent folder.",
            defaultDisplay = "cd ..",
            searchTags = listOf("cd ..", "parent directory", "up")
        ),
        CatalogItem(
            id = "bash_ls_la",
            name = "List Detailed (ls -la)",
            keyString = "ls -la",
            category = CatalogCategory.BASH_READLINE,
            description = "List all files including hidden dotfiles with permissions, size, and dates.",
            defaultDisplay = "ls -la",
            searchTags = listOf("ls", "list", "directory contents", "hidden files")
        ),
        CatalogItem(
            id = "bash_history",
            name = "Show History (history)",
            keyString = "history",
            category = CatalogCategory.BASH_READLINE,
            description = "Print numbered shell execution history.",
            defaultDisplay = "history",
            searchTags = listOf("history", "commands", "log")
        ),
        CatalogItem(
            id = "bash_clear",
            name = "Clear Command (clear)",
            keyString = "clear",
            category = CatalogCategory.BASH_READLINE,
            description = "Type clear and execute to reset terminal scrollback.",
            defaultDisplay = "clear",
            searchTags = listOf("clear", "cls", "reset")
        ),
        CatalogItem(
            id = "bash_chmod_x",
            name = "Make Executable (chmod +x )",
            keyString = "chmod +x ",
            category = CatalogCategory.BASH_READLINE,
            description = "Add executable permission flag to a shell script or binary.",
            defaultDisplay = "+x",
            searchTags = listOf("chmod", "executable", "permissions", "script")
        ),
        CatalogItem(
            id = "bash_source",
            name = "Reload Bashrc (source ~/.bashrc)",
            keyString = "source ~/.bashrc",
            category = CatalogCategory.BASH_READLINE,
            description = "Reload shell environment variables, aliases, and functions without restarting.",
            defaultDisplay = "src rc",
            searchTags = listOf("source", "bashrc", "reload", "env")
        ),

        // ─── 8. Pipes, Redirection & Operators ────────────────────────────────────────
        CatalogItem(
            id = "pipe_std",
            name = "Standard Pipe ( | )",
            keyString = "|",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Pipe standard output (stdout) of left command into standard input of right command.",
            defaultDisplay = "|",
            searchTags = listOf("pipe", "pipeline", "stdout", "stream")
        ),
        CatalogItem(
            id = "pipe_grep",
            name = "Filter Lines ( | grep )",
            keyString = " | grep ",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Search and filter piped output by keyword or regular expression.",
            defaultDisplay = "| grep",
            searchTags = listOf("grep", "filter", "search output", "pipe")
        ),
        CatalogItem(
            id = "pipe_awk",
            name = "Pattern Processor ( | awk )",
            keyString = " | awk ",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Extract columns and process text fields from stream.",
            defaultDisplay = "| awk",
            searchTags = listOf("awk", "columns", "format", "pipe")
        ),
        CatalogItem(
            id = "pipe_sed",
            name = "Stream Editor ( | sed )",
            keyString = " | sed ",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Perform search, replace, and text transformations on incoming stream.",
            defaultDisplay = "| sed",
            searchTags = listOf("sed", "stream editor", "replace", "pipe")
        ),
        CatalogItem(
            id = "pipe_xargs",
            name = "Build Arguments ( | xargs )",
            keyString = " | xargs ",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Convert standard input items into command line arguments for target command.",
            defaultDisplay = "| xargs",
            searchTags = listOf("xargs", "arguments", "batch", "pipe")
        ),
        CatalogItem(
            id = "pipe_tee",
            name = "Tee to File & Screen ( | tee )",
            keyString = " | tee ",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Duplicate stream output: write to a specified file while simultaneously displaying on screen.",
            defaultDisplay = "| tee",
            searchTags = listOf("tee", "duplicate", "log file", "pipe")
        ),
        CatalogItem(
            id = "pipe_less",
            name = "Pager View ( | less)",
            keyString = " | less",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Pipe long output into interactive scrollable pager.",
            defaultDisplay = "| less",
            searchTags = listOf("less", "pager", "scroll", "read")
        ),
        CatalogItem(
            id = "redir_err2out",
            name = "Redirect Stderr to Stdout (2>&1)",
            keyString = "2>&1",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Merge error stream (descriptor 2) into standard output (descriptor 1) so errors can be piped.",
            defaultDisplay = "2>&1",
            searchTags = listOf("2>&1", "stderr", "stdout", "merge", "errors")
        ),
        CatalogItem(
            id = "redir_append",
            name = "Append Output (>>)",
            keyString = ">>",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Append command stdout to the end of a destination file without overwriting.",
            defaultDisplay = ">>",
            searchTags = listOf(">>", "append", "redirect", "file")
        ),
        CatalogItem(
            id = "redir_overwrite",
            name = "Overwrite Output (>)",
            keyString = ">",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Redirect stdout to a destination file, overwriting existing contents.",
            defaultDisplay = ">",
            searchTags = listOf(">", "overwrite", "redirect", "output")
        ),
        CatalogItem(
            id = "redir_input",
            name = "Input Redirection (<)",
            keyString = "<",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Redirect file contents into command standard input.",
            defaultDisplay = "<",
            searchTags = listOf("<", "input", "stdin", "read file")
        ),
        CatalogItem(
            id = "op_and",
            name = "Logical AND (&&)",
            keyString = "&&",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Chain commands: execute subsequent command only if prior command exits with code 0 (success).",
            defaultDisplay = "&&",
            searchTags = listOf("&&", "and", "chain", "success")
        ),
        CatalogItem(
            id = "op_or",
            name = "Logical OR (||)",
            keyString = "||",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Chain commands: execute subsequent command only if prior command exits with error (non-zero).",
            defaultDisplay = "||",
            searchTags = listOf("||", "or", "failover", "error")
        ),
        CatalogItem(
            id = "op_semicolon",
            name = "Sequence Separator (;)",
            keyString = ";",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Execute commands in sequential order regardless of success or failure.",
            defaultDisplay = ";",
            searchTags = listOf(";", "semicolon", "sequence")
        ),
        CatalogItem(
            id = "op_background",
            name = "Run in Background (&)",
            keyString = "&",
            category = CatalogCategory.PIPES_OPERATORS,
            description = "Spawn command asynchronously as a background job, immediately freeing shell prompt.",
            defaultDisplay = "&",
            searchTags = listOf("&", "background", "daemon", "async")
        ),

        // ─── Symbols & Characters ─────────────────────────────────────────────────────
        CatalogItem(id = "sym_tilde", name = "Tilde (~)", keyString = "~", category = CatalogCategory.PIPES_OPERATORS, description = "Home directory shorthand.", defaultDisplay = "~"),
        CatalogItem(id = "sym_slash", name = "Slash (/)", keyString = "/", category = CatalogCategory.PIPES_OPERATORS, description = "Directory path separator and root indicator.", defaultDisplay = "/"),
        CatalogItem(id = "sym_bslash", name = "Backslash (\\)", keyString = "\\", category = CatalogCategory.PIPES_OPERATORS, description = "Character escape symbol and line continuation.", defaultDisplay = "\\"),
        CatalogItem(id = "sym_dash", name = "Dash / Minus (-)", keyString = "-", category = CatalogCategory.PIPES_OPERATORS, description = "Command flag prefix.", defaultDisplay = "-"),
        CatalogItem(id = "sym_underscore", name = "Underscore (_)", keyString = "_", category = CatalogCategory.PIPES_OPERATORS, description = "Identifier and variable naming character.", defaultDisplay = "_"),
        CatalogItem(id = "sym_dollar", name = "Dollar Symbol ($)", keyString = "$", category = CatalogCategory.PIPES_OPERATORS, description = "Variable expansion and shell prompt indicator.", defaultDisplay = "$"),
        CatalogItem(id = "sym_asterisk", name = "Asterisk / Wildcard (*)", keyString = "*", category = CatalogCategory.PIPES_OPERATORS, description = "Filename wildcard match all.", defaultDisplay = "*"),
        CatalogItem(id = "sym_quote", name = "Double Quote (\")", keyString = "\"", category = CatalogCategory.PIPES_OPERATORS, description = "String quote with variable interpolation.", defaultDisplay = "\""),
        CatalogItem(id = "sym_squote", name = "Single Quote (')", keyString = "'", category = CatalogCategory.PIPES_OPERATORS, description = "Literal string quote without variable expansion.", defaultDisplay = "'"),
        CatalogItem(id = "sym_backtick", name = "Backtick (`)", keyString = "`", category = CatalogCategory.PIPES_OPERATORS, description = "Command substitution backtick.", defaultDisplay = "`")
    )
}
