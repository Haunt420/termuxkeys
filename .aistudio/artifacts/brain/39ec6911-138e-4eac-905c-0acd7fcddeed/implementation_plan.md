# TermuxKeys Studio - Termux Extra Keys Configurator, Catalog & Reference Hub

TermuxKeys Studio is a visual layout workbench, interactive terminal sandbox, key function catalog, and comprehensive reference manual for Android Termux users. It allows users to build, preview, test, and export customized multi-row extra keys configurations (`termux.properties`) directly to their Termux environment while providing an exhaustive library of terminal shortcuts, functions, and documentation.

***

## User Review & Critical Decisions

> [!IMPORTANT]
> The following enhancements and requirements have been incorporated into the revised specification:

- **Exhaustive Key & Function Catalog**:
  - A searchable catalog with 100+ organized key definitions, control codes, Termux triggers, shell macros, and text processing sequences.
  - One-tap "Add to Keypad" or "Assign as Popup" directly from the catalog.
- **Dedicated Reference Material & Cheatsheet Hub**:
  - Full in-app documentation on `termux.properties` syntax and nested JSON-like bracket rules.
  - Complete GNU Nano shortcut manual with explanations and key combo details.
  - Bash/Zsh Readline navigation and line-editing cheat sheet.
  - Unix piping, redirection (stdin/stdout/stderr), and stream handling reference.
  - Interactive search across all reference topics with 1-tap "Copy" and "Insert Key" actions.
- **Termux Export & Real-Time Preview**:
  - Storage Access Framework (SAF) `.termux` folder export.
  - Auto-generated runnable Termux shell script (`mkdir -p ~/.termux && cat << 'EOF' > ~/.termux/termux.properties ... EOF && termux-reload-settings`).
  - Interactive terminal sandbox with responsive cursor, shell prompt simulation, and dedicated GNU Nano editor simulator mode.

***

## 1. Overview & Core Concept

Termux on Android lacks physical keys (`ESC`, `CTRL`, `ALT`, `TAB`, arrows, pipes, function keys). While Termux enables custom keys via `termux.properties`, creating and fine-tuning these configurations by hand is notoriously cumbersome due to strict array formatting and complex escape sequences.

TermuxKeys Studio provides an all-in-one studio:
1. **Interactive Layout Canvas**: Drag-and-drop / swap keycaps, manage up to 4 rows, configure primary actions, labels, and swipe-up popup keys.
2. **Exhaustive Key & Shortcut Catalog**: Searchable database of standard keys, function keys (`F1`-`F12`), control sequences (SIGINT, SIGTSTP, EOF), readline shortcuts, editor macros, and piping constructs.
3. **Interactive Terminal Sandbox & Nano Simulator**: Real-time tactile feedback with terminal cursor simulation, command buffer, and interactive GNU Nano 7.x mockup mode.
4. **Comprehensive Reference Material Hub**: Built-in searchable documentation for Termux properties syntax, GNU Nano commands, Readline shortcuts, and Unix I/O redirection.
5. **Multi-Format Export**: One-tap bash command, direct `.termux/termux.properties` SAF file saving, and clipboard export.

***

## 2. User Experience & Visual Design

### Aesthetic Direction
- **Cyberpunk / Terminal Dark Theme**: Deep dark slate canvas (`#0D1117`), elevated card surfaces (`#161B22`), crisp borders (`#30363D`), matrix emerald accents (`#2EA043` / `#3FB950`), cyber cyan (`#58A6FF`), and amber alert highlights (`#E3B341`).
- **Tactile Keycap Styling**: Realistic Android keyboard aesthetic with high-contrast glyphs, modifier states, macro tags, and corner indicator dots for popup keys.
- **Clear Information Density**: Tabs and bottom sheets for switching seamlessly between the **Visual Key Editor**, **Terminal Sandbox**, **Key Catalog Picker**, and **Reference Hub**.

### Key User Flows

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                             MAIN WORKSPACE TABS                             │
├─────────────────────┬───────────────────────┬───────────────────────────────┤
│ [1] Keypad Editor   │ [2] Terminal Sandbox  │ [3] Key Catalog & Reference   │
├─────────────────────┴───────────────────────┴───────────────────────────────┤
│                                                                             │
│  TAB 1: KEYPAD EDITOR & LIVE PREVIEW                                        │
│  • Visual multi-row layout grid (Drag/Drop, Move, Edit, Delete)             │
│  • Live Extra Keys Bar preview with instant touch response                  │
│  • Quick inspector for Key, Display Label, and Swipe-Up Popup               │
│                                                                             │
│  TAB 2: INTERACTIVE TERMINAL & NANO SIMULATOR                               │
│  • Shell Mode: live prompt ($ _), command history, interactive keys         │
│  • Nano Mode: simulated GNU nano buffer responding to ^O, ^X, ^W, ^K, ^U   │
│                                                                             │
│  TAB 3: KEY CATALOG & REFERENCE MATERIAL HUB                                │
│  • Search Bar: Instant filter across all shortcuts, keys, and docs          │
│  • Categorized Catalog: Modifiers, Termux actions, Nano, Bash, Pipes        │
│  • Reference Guide: Termux properties syntax, Nano manual, Readline docs   │
│  • Quick Actions: "Add to Current Row", "Copy Macro", "Inspect Details"     │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│ BOTTOM ACTION DOCK: [Preset Library]  [Add Key]  [Catalog]  [Export Config] │
└─────────────────────────────────────────────────────────────────────────────┘
```

***

## 3. Comprehensive Key Catalog & Reference Material Structure

### 3.1 Exhaustive Key & Function Catalog

The built-in catalog provides instant selection across eight curated domains:

1. **Modifiers & Standard Keys**:
   - `CTRL`, `ALT`, `ESC`, `TAB`, `ENTER`, `BKSP`, `DEL`, `INS`
2. **Navigation & Cursor**:
   - `UP`, `DOWN`, `LEFT`, `RIGHT`, `HOME`, `END`, `PGUP`, `PGDN`
3. **Function Keys**:
   - `F1` through `F12` with standard terminal VT escape mappings
4. **Termux-Specific Triggers**:
   - `DRAWER` (toggle Termux side navigation drawer)
   - `KEYBOARD` (toggle Android software keyboard visibility)
   - `PASTE` (paste clipboard text directly into terminal session)
5. **Control Sequences & Signals**:
   - `Ctrl+C` (SIGINT - interrupt active process)
   - `Ctrl+D` (EOF / logout current shell)
   - `Ctrl+Z` (SIGTSTP - suspend job to background)
   - `Ctrl+L` (Clear terminal display screen)
   - `Ctrl+R` (Incremental reverse history search)
   - `Ctrl+A` / `Ctrl+E` (Jump to beginning / end of line)
   - `Ctrl+U` / `Ctrl+K` (Erase from cursor to beginning / end of line)
   - `Ctrl+W` (Erase preceding word)
6. **GNU Nano Shortcuts & Commands**:
   - `^O` (WriteOut / Save current buffer)
   - `^X` (Exit editor)
   - `^W` (Where Is / Search text)
   - `^K` (Cut current line to cutbuffer)
   - `^U` (Uncut / Paste line from cutbuffer)
   - `^J` (Justify current paragraph)
   - `^C` (Show cursor position and line info)
   - `^G` (Display Nano help screen)
   - `^\` (Where Is and Replace)
   - `Alt+U` (Undo last edit)
   - `Alt+E` (Redo last edit)
   - `Alt+A` (Mark text region / visual select)
7. **Bash & Zsh Shell Shortcuts**:
   - `sudo !!` (Run previous command with elevated privileges)
   - `!!` (Repeat last command)
   - `!$` (Last argument of preceding command)
   - `cd ~`, `cd -`, `cd ..`
   - `ls -la`, `clear`, `exit`, `history`
   - `chmod +x `, `kill -9 `
8. **Piping, Redirection & Operators**:
   - `|` (Pipe stdout to next program)
   - `| grep ` (Filter output lines)
   - `| awk ` (Pattern scanning & processing)
   - `| sed ` (Stream editor filter)
   - `| xargs ` (Build argument lists from input)
   - `| tee ` (Tee output to file and screen)
   - `2>&1` (Redirect stderr to stdout)
   - `>>` (Append stdout to file)
   - `>` (Overwrite stdout to file)
   - `&&` (Execute if preceding command succeeded)
   - `||` (Execute if preceding command failed)
   - `;` (Command sequence separator)
   - `~`, `/`, `\`, `_`, `-`, `*`, `?`, `"`, `'`, '`'

### 3.2 Exhaustive Reference Material Menu

The Reference Material Hub provides detailed, interactive documentation sections:

1. **Termux Properties Configuration Manual**:
   - Detailed breakdown of `~/.termux/termux.properties`.
   - Nested matrix array grammar: `extra-keys = [['ESC','TAB'],['CTRL','ALT']]`.
   - Advanced dictionary/object syntax: `{'key': 'KEY', 'display': 'LABEL', 'popup': 'POPUP_KEY'}`.
   - Popup macro expansion: `{'key': 'KEY', 'popup': {'macro': 'TEXT', 'display': 'LABEL'}}`.
   - Extra keys styling parameters: `extra-keys-style = default` vs `arrows-only`.
   - How `termux-reload-settings` reloads the UI without restarting sessions.
2. **GNU Nano Complete Reference Guide**:
   - Comprehensive handbook of GNU Nano shortcuts (File management, Search/Replace, Deletion/Pasting, Motion/Movement, Configuration options).
   - Practical workflows for Termux mobile editing.
3. **Bash/Zsh Command-Line Readline Reference**:
   - Movement, editing, completion, history, and process control keys explained with practical use cases.
4. **Unix I/O Redirection & Piping Cheat Sheet**:
   - Explanation of standard streams: 0 (stdin), 1 (stdout), 2 (stderr).
   - Redirection operators (`>`, `>>`, `<`, `2>&1`, `&>`).
   - Piping patterns, teeing streams, and background execution (`&`, `nohup`).

***

## 4. Technical Architecture & Component Hierarchy

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          ARCHITECTURE DIAGRAM                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                         Jetpack Compose UI                          │   │
│   │  ┌─────────────────────────┐  ┌──────────────────────────────────┐  │   │
│   │  │ KeypadEditorView        │  │ TerminalSandboxView              │  │   │
│   │  │ (Multi-row Canvas,      │  │ (Interactive Shell Prompt &      │  │   │
│   │  │  Drag/Reorder, Keycaps) │  │  GNU Nano Simulator)             │  │   │
│   │  └────────────┬────────────┘  └─────────────────┬────────────────┘  │   │
│   │               │                                 │                   │   │
│   │  ┌────────────┴────────────┐  ┌─────────────────┴────────────────┐  │   │
│   │  │ KeyCatalogPickerModal   │  │ ReferenceMaterialHubView         │  │   │
│   │  │ (Searchable 8-category  │  │ (Termux syntax, Nano guide,      │  │   │
│   │  │  shortcuts & functions) │  │  Readline, Piping docs)          │  │   │
│   │  └────────────┬────────────┘  └─────────────────┬────────────────┘  │   │
│   │               │                                 │                   │   │
│   │  ┌────────────┴────────────┐  ┌─────────────────┴────────────────┐  │   │
│   │  │ KeyInspectorDialog      │  │ ExportBottomSheet                │  │   │
│   │  │ (Key, Label, Popup edit)│  │ (SAF export, Bash script, Copy)  │  │   │
│   │  └─────────────────────────┘  └──────────────────────────────────┘  │   │
│   └───────────────────────────────────┬─────────────────────────────────┘   │
│                                       │ State & Events                      │
│                                       ▼                                     │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                     TermuxKeysViewModel (StateFlow)                 │   │
│   │  • activeLayout: List<List<KeyItem>> (Current custom rows)          │   │
│   │  • terminalState: TerminalState (Buffer, Cursor, NanoMode)          │   │
│   │  • catalogSearchQuery: String & filtered catalog items              │   │
│   │  • activeReferenceSection: ReferenceTopic                           │   │
│   │  • savedPresets: List<KeyPreset> (Built-in + Custom User Presets)   │   │
│   └───────────────┬─────────────────────────────────────┬───────────────┘   │
│                   │                                     │                   │
│                   ▼                                     ▼                   │
│   ┌─────────────────────────────┐       ┌───────────────────────────────┐   │
│   │ PropertiesFormatterEngine   │       │ Preset & Reference Repository │   │
│   │ (Parses & formats Termux    │       │ • Room AppDatabase            │   │
│   │ properties nested syntax)   │       │ • BuiltinCatalogData          │   │
│   └─────────────────────────────┘       │ • ReferenceDocumentationData  │   │
│                                         └───────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Key Data Entities

```kotlin
data class KeyItem(
    val id: String = UUID.randomUUID().toString(),
    val key: String,            // e.g. "ESC", "CTRL", "ALT", "|", "UP", "nano"
    val display: String = "",    // Custom label (e.g. "⎋", "^", "Pipe")
    val popup: KeyPopup? = null  // Swipe-up popup action
)

data class KeyPopup(
    val key: String,            // e.g. "F1", "~", "HOME"
    val display: String = ""
)

data class CatalogItem(
    val id: String,
    val name: String,
    val keyString: String,
    val category: CatalogCategory,
    val description: String,
    val defaultDisplay: String = "",
    val suggestedPopup: String? = null
)

data class ReferenceTopic(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val contentMarkdown: String,
    val relatedKeys: List<String>
)
```

***

## 5. Verification & Testing Strategy
- Local JVM unit & Robolectric tests:
  - `PropertiesFormatterEngineTest`: validates standard array format, nested dictionary objects, and popup macro serialization.
  - `CatalogSearchTest`: validates category filtering and multi-keyword search across all 100+ catalog items.
  - `TerminalStateTest`: validates simulated buffer updates, Nano mode switching, and key execution.
- Build and compilation verification using `compile_applet`.
