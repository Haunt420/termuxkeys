package com.example.data

import com.example.model.ScriptRecipe
import com.example.model.ScriptType

data class TutorialChapter(
    val id: String,
    val title: String,
    val subtitle: String,
    val markdownGuide: String,
    val runnableSnippet: String,
    val relatedRecipeId: String? = null
)

object BuiltinShortcutRecipes {

    val chapters: List<TutorialChapter> = listOf(
        TutorialChapter(
            id = "tut_intro",
            title = "1. How Extra Keys & Shell Shortcuts Connect",
            subtitle = "Bridging touch keycaps with Linux shell execution",
            markdownGuide = """
When you tap an extra key in Termux, it injects characters directly into your shell's STDIN buffer as if typed from a physical keyboard.

By defining custom aliases in '~/.bashrc' or standalone scripts in '~/bin', you can turn complex multi-step pipelines into a single tap on your extra keys bar!

Workflow Overview:
1. Write an alias or function in ~/.bashrc.
2. Bind the alias name (e.g. 'update\n') as a macro to an extra key.
3. When tapped, Termux types the alias and executes it instantly!
            """.trimIndent(),
            runnableSnippet = "echo \"alias ll='ls -lah'\" >> ~/.bashrc && source ~/.bashrc"
        ),

        TutorialChapter(
            id = "tut_bash_aliases",
            title = "2. Crafting Persistent Bash & Zsh Aliases",
            subtitle = "One-word shortcuts for lengthy commands",
            markdownGuide = """
An alias replaces a single word with an arbitrary command string.

Syntax:
alias name='command --flag argument'

Best Practices for Termux:
• Always place aliases inside '~/.bashrc' (or '~/.zshrc' if using Zsh).
• Avoid naming aliases after existing core binaries (e.g., don't name an alias 'ls' without keeping fallback flags).
• Use trailing newlines in Termux macros so the command executes immediately upon tapping.
            """.trimIndent(),
            runnableSnippet = "alias sysup='pkg update -y && pkg upgrade -y && pkg autoclean'"
        ),

        TutorialChapter(
            id = "tut_bash_functions",
            title = "3. Bash Functions with Dynamic Arguments",
            subtitle = "Accepting parameters ($1, $2, $@) on the fly",
            markdownGuide = """
When an alias isn't enough because you need to pass dynamic arguments (like a commit message or a search pattern), use a Bash function.

Syntax:
myfunc() {
  local target="${'$'}1"
  echo "Processing ${'$'}target..."
  command "${'$'}target"
}

Arguments:
• ${'$'}1, ${'$'}2 : First and second arguments
• ${'$'}@ : All passed arguments as separate words
• ${'$'}? : Exit status of the previous command
            """.trimIndent(),
            runnableSnippet = "gpush() { git add . && git commit -m \"${'$'}*\" && git push; }"
        ),

        TutorialChapter(
            id = "tut_python_scripts",
            title = "4. Python Automation Scripts in Termux",
            subtitle = "Building standalone CLI utilities in ~/bin",
            markdownGuide = """
Termux has full Python 3 support ('pkg install python'). You can create executable scripts that can be invoked from anywhere by placing them in '~/bin' (or '${'$'}PREFIX/bin').

Steps:
1. Add the Termux Python shebang at line 1:
   #!/data/data/com.termux/files/usr/bin/python
2. Make the file executable:
   chmod +x ~/bin/myscript.py
3. Map an extra key to execute 'myscript.py\n'.
            """.trimIndent(),
            runnableSnippet = "mkdir -p ~/bin && chmod +x ~/bin/* 2>/dev/null || true"
        ),

        TutorialChapter(
            id = "tut_popups",
            title = "5. Leveraging Swipe-Up Popup Macros",
            subtitle = "Two commands on a single button",
            markdownGuide = """
Every Termux extra key supports an optional swipe-up popup action.

Pro-Tip Architecture:
• Tap: runs the non-destructive or read-only action (e.g. 'git status\n' or 'ls').
• Swipe-Up: runs the write or execute action (e.g. 'git push\n' or 'ls -la').

This doubles the functional capacity of your 7-column extra keys bar without consuming extra screen real estate!
            """.trimIndent(),
            runnableSnippet = "{key: 'git', popup: {macro: 'git status\n', display: 'status'}}"
        )
    )

    val recipes: List<ScriptRecipe> = listOf(
        ScriptRecipe(
            id = "rec_git_quick_sync",
            title = "Git Rapid Sync (Add, Commit & Push)",
            type = ScriptType.BASH_FUNCTION,
            category = "Git & Version Control",
            summary = "One-tap function that stages all changes, prompts or uses arguments for commit message, and pushes to remote.",
            explanation = "Automates the tedious git add, commit, and push sequence into a single callable command.",
            codeBody = """
# Place in ~/.bashrc:
quickpush() {
  local msg="${'$'}{1:-Quick update from Termux}"
  git add .
  git commit -m "${'$'}msg"
  git push
  echo "Pushed with commit: ${'$'}msg"
}
            """.trimIndent(),
            suggestedKeyDisplay = "G-PUSH",
            suggestedKeyCode = "quickpush \"Auto-sync\"\n",
            suggestedPopup = "git status\n",
            sampleUsage = "quickpush \"Fixed layout parser\"",
            steps = listOf(
                "Open ~/.bashrc in nano: nano ~/.bashrc",
                "Paste the quickpush function code",
                "Run: source ~/.bashrc",
                "Bind extra key to: quickpush\n"
            )
        ),

        ScriptRecipe(
            id = "rec_python_server",
            title = "Instant Python Local HTTP Server",
            type = ScriptType.PYTHON_SCRIPT,
            category = "Web & Networking",
            summary = "Spawns a local HTTP file server on port 8080 binding to all interfaces with automatic IP detection.",
            explanation = "Allows immediate wireless file transfer from your phone to any computer on the local WiFi network.",
            codeBody = """
#!/data/data/com.termux/files/usr/bin/python
import http.server
import socketserver
import socket

PORT = 8080
hostname = socket.gethostname()
local_ip = socket.gethostbyname(hostname)

print(f"[*] Serving files at http://{local_ip}:{PORT}")
Handler = http.server.SimpleHTTPRequestHandler
with socketserver.TCPServer(("", PORT), Handler) as httpd:
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n[*] Server stopped.")
            """.trimIndent(),
            suggestedKeyDisplay = "SRV 8080",
            suggestedKeyCode = "python3 -m http.server 8080\n",
            suggestedPopup = "pkill -f http.server\n",
            sampleUsage = "serve",
            steps = listOf(
                "Install python: pkg install python -y",
                "Create script: nano ~/bin/serve && chmod +x ~/bin/serve",
                "Bind extra key to: serve\n"
            )
        ),

        ScriptRecipe(
            id = "rec_sys_update",
            title = "Termux Package Update & Clean",
            type = ScriptType.BASH_ALIAS,
            category = "System Maintenance",
            summary = "Updates package lists, upgrades installed packages, and cleans cached APKs.",
            explanation = "Consolidates apt/pkg cache maintenance into an idempotent one-liner.",
            codeBody = "alias sysup='pkg update -y && pkg upgrade -y && pkg autoclean && echo \"[OK] Termux updated!\"'",
            suggestedKeyDisplay = "SYS-UP",
            suggestedKeyCode = "pkg up -y && pkg clean\n",
            suggestedPopup = "pkg list-installed\n",
            sampleUsage = "sysup",
            steps = listOf(
                "Append alias to ~/.bashrc",
                "Reload: source ~/.bashrc",
                "Bind extra key to: sysup\n"
            )
        ),

        ScriptRecipe(
            id = "rec_json_prettify",
            title = "Python JSON Pipe Prettifier",
            type = ScriptType.PYTHON_SCRIPT,
            category = "Piping & Text Processing",
            summary = "CLI stream filter that reads raw JSON from stdin and outputs formatted, indented colored JSON.",
            explanation = "Essential when working with curl, APIs, or database dumps in Termux.",
            codeBody = """
# Add alias to ~/.bashrc:
alias pjson='python3 -c "import sys, json; print(json.dumps(json.loads(sys.stdin.read()), indent=2))"'
            """.trimIndent(),
            suggestedKeyDisplay = "| JSON",
            suggestedKeyCode = " | pjson\n",
            suggestedPopup = " | jq .\n",
            sampleUsage = "curl -s https://api.github.com | pjson",
            steps = listOf(
                "Append alias to ~/.bashrc",
                "Pipe any JSON stream: curl ... | pjson"
            )
        ),

        ScriptRecipe(
            id = "rec_tmux_attach",
            title = "Tmux Persistent Session Manager",
            type = ScriptType.BASH_FUNCTION,
            category = "Terminal Multiplexing",
            summary = "Connects to an existing background terminal session or creates a new persistent session.",
            explanation = "Prevents losing active terminal jobs when Android puts Termux in the background.",
            codeBody = """
tms() {
  local sess="${'$'}{1:-main}"
  tmux attach-session -t "${'$'}sess" 2>/dev/null || tmux new-session -s "${'$'}sess"
}
            """.trimIndent(),
            suggestedKeyDisplay = "TMUX",
            suggestedKeyCode = "tms\n",
            suggestedPopup = "tmux kill-server\n",
            sampleUsage = "tms",
            steps = listOf(
                "Install tmux: pkg install tmux -y",
                "Add tms() function to ~/.bashrc",
                "Bind extra key to: tms\n"
            )
        ),

        ScriptRecipe(
            id = "rec_venv_activate",
            title = "Python VirtualEnv Auto-Activator",
            type = ScriptType.BASH_FUNCTION,
            category = "Python Development",
            summary = "Detects and activates virtual environments (venv, .venv, or env) in the current directory.",
            explanation = "Eliminates repetitive typing of 'source ./venv/bin/activate'.",
            codeBody = """
act() {
  for dir in venv .venv env .env; do
    if [ -f "${'$'}dir/bin/activate" ]; then
      source "${'$'}dir/bin/activate"
      echo "[*] Activated (${'$'}dir)"
      return 0
    fi
  done
  echo "[-] No virtualenv found in $(pwd)"
  return 1
}
            """.trimIndent(),
            suggestedKeyDisplay = "ACT-VENV",
            suggestedKeyCode = "act\n",
            suggestedPopup = "deactivate\n",
            sampleUsage = "act",
            steps = listOf(
                "Add act() function to ~/.bashrc",
                "Call 'act' inside any project folder"
            )
        )
    )
}
