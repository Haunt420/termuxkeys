package com.example.data

import com.example.model.KeyItem
import com.example.model.KeyPopup
import com.example.model.KeyPreset

object BuiltinPresetsLibrary {

    val userClonedPreset = KeyPreset(
        id = "preset_user_exact_clone",
        name = "My Termux UI (Pixel Clone)",
        category = "Custom Setup",
        description = "Exact pixel-for-pixel layout from user Termux screenshot with 7-column dual row extra keys.",
        rows = listOf(
            listOf(
                KeyItem(key = "TAB", display = "-TAB-\n-GO-", popup = KeyPopup(key = "TAB", display = "TAB")),
                KeyItem(key = "\\", display = "\\"),
                KeyItem(key = "PGUP", display = "PGUP"),
                KeyItem(key = "PASTE", display = "PASTE"),
                KeyItem(key = "UP", display = "↑"),
                KeyItem(key = "ls ", display = "LS ›", popup = KeyPopup(key = "ls -la", display = "ls -la")),
                KeyItem(key = "ALT", display = "ALT")
            ),
            listOf(
                KeyItem(key = "TAB", display = "⇆"),
                KeyItem(key = "END", display = "END"),
                KeyItem(key = "PGDN", display = "PGDN"),
                KeyItem(key = "LEFT", display = "←"),
                KeyItem(key = "DOWN", display = "↓"),
                KeyItem(key = "RIGHT", display = "→"),
                KeyItem(key = "CTRL", display = "CTRL")
            )
        )
    )

    val presets: List<KeyPreset> = listOf(
        userClonedPreset,
        KeyPreset(
            id = "preset_nano_master",
            name = "Nano Editor Master",
            category = "Nano Editor",
            description = "Optimized for mobile text editing in GNU nano with dedicated shortcuts for saving, exiting, search, cut, and paste.",
            rows = listOf(
                listOf(
                    KeyItem(key = "ESC", display = "ESC"),
                    KeyItem(key = "CTRL", display = "^O", popup = KeyPopup(key = "WriteOut", display = "Save")),
                    KeyItem(key = "CTRL", display = "^X", popup = KeyPopup(key = "Exit", display = "Quit")),
                    KeyItem(key = "CTRL", display = "^W", popup = KeyPopup(key = "WhereIs", display = "Find")),
                    KeyItem(key = "CTRL", display = "^K", popup = KeyPopup(key = "CutText", display = "Cut")),
                    KeyItem(key = "CTRL", display = "^U", popup = KeyPopup(key = "UnCut", display = "Paste")),
                    KeyItem(key = "UP", display = "▲")
                ),
                listOf(
                    KeyItem(key = "TAB", display = "TAB"),
                    KeyItem(key = "ALT", display = "ALT"),
                    KeyItem(key = "CTRL", display = "^J", popup = KeyPopup(key = "Justify", display = "Wrap")),
                    KeyItem(key = "CTRL", display = "^C", popup = KeyPopup(key = "CurPos", display = "Info")),
                    KeyItem(key = "CTRL", display = "^G", popup = KeyPopup(key = "Help", display = "Help")),
                    KeyItem(key = "LEFT", display = "◀"),
                    KeyItem(key = "DOWN", display = "▼"),
                    KeyItem(key = "RIGHT", display = "▶")
                )
            )
        ),

        KeyPreset(
            id = "preset_termux_system",
            name = "Termux System & Touch Essentials",
            category = "Termux Essentials",
            description = "Essential navigation keys, drawer toggle, keyboard toggle, clipboard paste, and page scrolls.",
            rows = listOf(
                listOf(
                    KeyItem(key = "ESC", display = "ESC"),
                    KeyItem(key = "TAB", display = "TAB"),
                    KeyItem(key = "CTRL", display = "CTRL"),
                    KeyItem(key = "ALT", display = "ALT"),
                    KeyItem(key = "DRAWER", display = "☰", popup = KeyPopup(key = "KEYBOARD", display = "⌨")),
                    KeyItem(key = "PASTE", display = "📋"),
                    KeyItem(key = "UP", display = "▲")
                ),
                listOf(
                    KeyItem(key = "HOME", display = "HOME"),
                    KeyItem(key = "END", display = "END"),
                    KeyItem(key = "PGUP", display = "PGUP"),
                    KeyItem(key = "PGDN", display = "PGDN"),
                    KeyItem(key = "LEFT", display = "◀"),
                    KeyItem(key = "DOWN", display = "▼"),
                    KeyItem(key = "RIGHT", display = "▶")
                )
            )
        ),

        KeyPreset(
            id = "preset_bash_cli",
            name = "Bash & Zsh CLI Power Tools",
            category = "Shell & CLI",
            description = "Command-line productivity layout with history jumps, pipes, redirects, tildes, and enter.",
            rows = listOf(
                listOf(
                    KeyItem(key = "CTRL", display = "CTRL"),
                    KeyItem(key = "ALT", display = "ALT"),
                    KeyItem(key = "ESC", display = "ESC"),
                    KeyItem(key = "TAB", display = "TAB"),
                    KeyItem(key = "HOME", display = "HOME"),
                    KeyItem(key = "END", display = "END"),
                    KeyItem(key = "PGUP", display = "PGUP"),
                    KeyItem(key = "PGDN", display = "PGDN")
                ),
                listOf(
                    KeyItem(key = "|", display = "|", popup = KeyPopup(key = " | grep ", display = "grep")),
                    KeyItem(key = "&&", display = "&&", popup = KeyPopup(key = "||", display = "||")),
                    KeyItem(key = ">>", display = ">>", popup = KeyPopup(key = ">", display = ">")),
                    KeyItem(key = "~", display = "~"),
                    KeyItem(key = "/", display = "/"),
                    KeyItem(key = "-", display = "-"),
                    KeyItem(key = "_", display = "_"),
                    KeyItem(key = "ENTER", display = "↵")
                )
            )
        ),

        KeyPreset(
            id = "preset_piping_processing",
            name = "Piping & Unix Text Processing",
            category = "Piping & Filters",
            description = "Specialized stream manipulation bar with grep, sed, awk, xargs, tee, error redirection, and pipes.",
            rows = listOf(
                listOf(
                    KeyItem(key = "|", display = "|", popup = KeyPopup(key = " | grep ", display = "grep")),
                    KeyItem(key = "2>&1", display = "2>&1", popup = KeyPopup(key = " > /dev/null 2>&1", display = "silent")),
                    KeyItem(key = "&&", display = "&&", popup = KeyPopup(key = "||", display = "||")),
                    KeyItem(key = ">>", display = ">>", popup = KeyPopup(key = ">", display = ">")),
                    KeyItem(key = ";", display = ";", popup = KeyPopup(key = "&", display = "&")),
                    KeyItem(key = "*", display = "*", popup = KeyPopup(key = "?", display = "?")),
                    KeyItem(key = "ENTER", display = "↵")
                ),
                listOf(
                    KeyItem(key = " | awk ", display = "awk"),
                    KeyItem(key = " | sed ", display = "sed"),
                    KeyItem(key = " | xargs ", display = "xargs"),
                    KeyItem(key = " | tee ", display = "tee"),
                    KeyItem(key = " | less", display = "less"),
                    KeyItem(key = "BKSP", display = "⌫")
                )
            )
        ),

        KeyPreset(
            id = "preset_vim_minimalist",
            name = "Vim / Neovim Minimalist",
            category = "Terminal Editors",
            description = "Direct Vim normal mode and command key bindings for modal terminal editing.",
            rows = listOf(
                listOf(
                    KeyItem(key = "ESC", display = "ESC"),
                    KeyItem(key = ":", display = ":", popup = KeyPopup(key = ":w", display = ":w")),
                    KeyItem(key = "/", display = "/", popup = KeyPopup(key = ":q!", display = ":q!")),
                    KeyItem(key = "i", display = "i"),
                    KeyItem(key = "v", display = "v"),
                    KeyItem(key = "^", display = "^"),
                    KeyItem(key = "$", display = "$")
                ),
                listOf(
                    KeyItem(key = "TAB", display = "TAB"),
                    KeyItem(key = "CTRL", display = "CTRL"),
                    KeyItem(key = "w", display = "w"),
                    KeyItem(key = "q", display = "q"),
                    KeyItem(key = "!", display = "!"),
                    KeyItem(key = "LEFT", display = "h"),
                    KeyItem(key = "DOWN", display = "j"),
                    KeyItem(key = "UP", display = "k"),
                    KeyItem(key = "RIGHT", display = "l")
                )
            )
        )
    )
}
