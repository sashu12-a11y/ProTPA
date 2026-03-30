# Contributing to ProTPA

Thank you for your interest in contributing to ProTPA! We welcome contributions from the community. This document provides guidelines and information for contributors.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Pull Request Process](#pull-request-process)
- [Code Style](#code-style)
- [Testing](#testing)
- [Reporting Issues](#reporting-issues)

## 🤝 Code of Conduct

This project follows our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you agree to uphold this code.

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git
- A Minecraft server for testing (Paper recommended)

### Setup

1. **Fork** the repository on GitHub
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/your-username/ProTPA.git
   cd ProTPA
   ```
3. **Set up** the development environment:
   ```bash
   mvn clean install
   ```
4. **Create** a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## 🔄 Development Workflow

1. **Choose an issue** or create one for the feature/bug you want to work on
2. **Create a branch** from `main` following the naming convention
3. **Make your changes** following our code style
4. **Write tests** for new features
5. **Test thoroughly** on a local Minecraft server
6. **Commit** your changes using conventional commits
7. **Push** to your fork and create a Pull Request

### Branch Naming Convention

- `feature/description` - New features
- `fix/description` - Bug fixes
- `docs/description` - Documentation
- `refactor/description` - Code refactoring
- `test/description` - Testing

## 📝 Commit Message Guidelines

We use [Conventional Commits](https://conventionalcommits.org/) format:

```
type(scope): description

[optional body]

[optional footer]
```

### Types

- `feat` - New features
- `fix` - Bug fixes
- `docs` - Documentation changes
- `style` - Code style changes (formatting, etc.)
- `refactor` - Code refactoring
- `test` - Adding or updating tests
- `chore` - Maintenance tasks

### Examples

```
feat: add new teleport animation system
fix: resolve RTP safe location bug
docs: update installation guide
refactor: simplify countdown manager logic
```

## 🔍 Pull Request Process

1. **Ensure** your PR description clearly describes the changes
2. **Reference** any related issues
3. **Include** screenshots/videos for UI changes
4. **Update** documentation if needed
5. **Wait** for review and address feedback
6. **Merge** once approved

### PR Template

Please use this template for your PR descriptions:

```markdown
## Description
Brief description of the changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
Describe how you tested the changes

## Screenshots
Add screenshots if applicable

## Checklist
- [ ] My code follows the project's style guidelines
- [ ] I have performed a self-review of my own code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix/feature works
- [ ] New and existing unit tests pass locally
```

## 💻 Code Style

### Java Conventions

- Use **camelCase** for variables and methods
- Use **PascalCase** for classes and interfaces
- Add **JavaDoc** comments for public APIs
- Keep methods under **50 lines** when possible
- Use **dependency injection** for managers
- Follow **SOLID principles**

### Example

```java
/**
 * Manages teleportation countdowns with animations
 */
public class CountdownManager {

    private final ProTPA plugin;

    /**
     * Starts a countdown with animations
     * @param player The player to teleport
     * @param onComplete Callback when countdown completes
     * @param onCancel Callback when countdown is cancelled
     */
    public void startCountdown(Player player, Runnable onComplete, Runnable onCancel) {
        // Implementation here
    }
}
```

## 🧪 Testing

### Unit Tests

Run tests with Maven:
```bash
mvn test
```

### Integration Testing

1. Build the plugin: `mvn clean package`
2. Copy `target/ProTPA.jar` to your test server
3. Test all features thoroughly
4. Check for console errors

### Manual Testing Checklist

- [ ] TPA requests work correctly
- [ ] TPAHERE requests work correctly
- [ ] RTP finds safe locations
- [ ] /back command works after death
- [ ] Animations display properly
- [ ] GUIs function correctly
- [ ] Permissions work as expected
- [ ] Configuration loads properly

## 🐛 Reporting Issues

### Bug Reports

Please use the [GitHub Issues](https://github.com/sashu12-a11y/ProTPA/issues) template and include:

- **Description**: Clear description of the issue
- **Steps to reproduce**: Step-by-step instructions
- **Expected behavior**: What should happen
- **Actual behavior**: What actually happens
- **Environment**: Server version, Java version, plugin version
- **Logs**: Relevant console/server logs
- **Screenshots**: If applicable

### Feature Requests

For new features, please:

- Check if the feature already exists or is planned
- Describe the feature clearly
- Explain why it would be useful
- Provide mockups if it's a UI feature

## 📞 Getting Help

- **Discussions**: Use [GitHub Discussions](https://github.com/sashu12-a11y/ProTPA/discussions) for questions
- **Discord**: Join our community server (link in README)
- **Email**: sashankc344@gmail.com

## 🙏 Recognition

Contributors will be recognized in the README and potentially added to a contributors file. Thank you for helping make ProTPA better!