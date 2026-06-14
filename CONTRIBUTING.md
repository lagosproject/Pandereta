# Contributing to Pandereta

Thank you for choosing to contribute to Pandereta! We welcome all improvements, bug fixes, and feature additions.

## 🛠️ Development Setup

1. Fork the repository on GitHub.
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Pandereta.git
   cd Pandereta
   ```
3. Open in Android Studio.
4. Verify the setup by running unit tests:
   ```bash
   ./gradlew testDebugUnitTest
   ```

## 🌿 Branching Strategy

- Always create a new branch from `main`/`master` for your changes:
  - For features: `feature/your-feature-name`
  - For bug fixes: `bugfix/your-bugfix-name`
  - For chores/docs: `chore/your-chore-name`

## 📝 Commit Guidelines

- Write clear, concise commit messages.
- Prefer conventional commit messages (e.g., `feat: add sound adjustment settings`, `fix: prevent null-pointer in sensor flow`).

## 🚀 Submitting a Pull Request

1. Ensure the project builds successfully without lint errors:
   ```bash
   ./gradlew lintDebug assembleDebug
   ```
2. Push your changes to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```
3. Open a Pull Request from your branch to the main repository.
4. Ensure your PR description lists the changes made and highlights what was tested.
