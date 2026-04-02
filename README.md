═# 🚀 ProTPA - Advanced Minecraft Teleportation Plugin

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20+-green.svg)](https://www.minecraft.net/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

> **Elevate your server's teleportation experience with stunning animations, intuitive GUIs, and robust safety features.**

ProTPA is a comprehensive Minecraft teleportation plugin that brings modern, animated teleportation mechanics to your server. Featuring TPA/TPAHERE requests, random teleportation, death location recovery, and beautiful visual effects, it provides the ultimate teleportation experience for players and server administrators.

## ✨ Key Features

### 🎯 **Advanced Teleportation Commands**
- **`/tpa <player>`** - Request to teleport to another player
- **`/tpahere <player>`** - Request to bring another player to your location
- **`/rtp`** - Random teleportation with intelligent safe location finding
- **`/back`** - Return to your last death location (within 5 minutes)

### 🎨 **Immersive Animations**
- **Color-coded countdown titles**: 3§a → 2§e → 1§c with smooth transitions
- **Synchronized sound effects**: Progressive pitch UI sounds
- **Particle effects**: CRIT particle bursts for visual feedback
- **Action bar updates**: Real-time countdown display
- **Fully configurable**: Customize all animations via config

### 🖥️ **Intuitive GUI Interfaces**
- **Request GUI**: Accept/deny teleport requests with a single click
- **World Selection**: Choose destination world for RTP with rainbow-colored items
- **Color-coded titles**: Proper color code conversion for all interfaces

### 🛡️ **Safety & Security Features**
- **Movement cancellation**: Teleport cancels if player moves during countdown
- **Combat protection**: Prevents teleportation during combat situations
- **Safe RTP locations**: Advanced algorithm finds valid air spaces above terrain
- **Permission system**: Granular permissions for all commands
- **Cooldown system**: Configurable delays between teleport requests

### ⚙️ **Highly Configurable**
- **YAML configuration**: Easy-to-edit `config.yml` and `messages.yml`
- **Color code support**: Full `&` to `§` conversion throughout
- **Sound customization**: Choose teleport and UI sounds
- **Particle effects**: Customize visual effects
- **Message localization**: Support for multiple languages
- **Toggle system**: Players can disable TPA requests

## 📋 Requirements

- **Minecraft Version**: 1.20 or higher
- **Server Software**: Paper, Purpur, Pufferfish, Spigot, or Bukkit
- **Java Version**: 17+
- **Memory**: Minimal impact on server performance

## 🚀 Installation

1. **Download** the latest release from [GitHub Releases](https://github.com/sashu12-a11y/ProTPA/releases)
2. **Place** `ProTPA.jar` in your server's `plugins/` folder
3. **Restart** your server or run `/reload`
4. **Configure** settings in `plugins/ProTPA/config.yml`
5. **Customize** messages in `plugins/ProTPA/messages.yml`

### Configuration Example

```yaml
# config.yml
teleport:
  delay: 3                    # Countdown seconds
  cooldown: 30               # Cooldown between requests

animations:
  enabled: true
  titles:
    fade-in: 0
    stay: 15
    fade-out: 0
  sounds:
    enabled: true
    tick: "UI_BUTTON_CLICK"
  particles:
    enabled: true
    type: "CRIT"

back:
  enabled: true
  expiry-seconds: 300         # 5 minutes

rtp:
  min-radius: 1000
  max-radius: 10000
  max-attempts: 10
```

## 📖 Usage Examples

### Basic Teleportation
```bash
# Request to teleport to a player
/tpa Steve

# Request player to come to you
/tpahere Alex

# Accept incoming request
/tpaaccept

# Random teleport
/rtp

# Return to death location
/back
```

### Permission Setup
```yaml
# Add to your permissions plugin
protpa.use: true          # Basic TPA commands
protpa.accept: true       # Accept requests
protpa.rtp: true          # Random teleport
protpa.back: true         # Death location recovery
protpa.toggle: true       # Toggle TPA requests
protpa.reload: op         # Admin reload command
```

## 🏗️ Project Structure

```
ProTPA/
├── src/main/java/me/yourname/protpa/     # Source code
│   ├── commands/                         # Command classes
│   ├── managers/                         # Manager classes
│   ├── listeners/                        # Event listeners
│   ├── data/                            # Data models
│   └── inventory/                        # GUI classes
├── src/main/resources/                   # Configuration files
│   ├── config.yml                       # Main configuration
│   ├── messages.yml                     # Message templates
│   ├── plugin.yml                       # Plugin metadata
│   └── logs.yml                         # Logging configuration
├── docs/                                # Documentation
├── .github/workflows/                   # CI/CD workflows
├── LICENSE                              # MIT License
├── README.md                           # This file
├── CONTRIBUTING.md                     # Contribution guidelines
├── CODE_OF_CONDUCT.md                  # Code of conduct
└── pom.xml                             # Maven configuration
```

## 🛠️ Development

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git

### Building from Source

```bash
# Clone the repository
git clone https://github.com/sashu12-a11y/ProTPA.git
cd ProTPA

# Build with Maven
mvn clean package

# The compiled JAR will be in target/ProTPA.jar
```

### Running Tests

```bash
mvn test
```

### Code Style
This project follows standard Java conventions:
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods focused and under 50 lines
- Use dependency injection for managers

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Workflow
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Make your changes and add tests
4. Commit using conventional commits: `git commit -m "feat: add new teleport animation"`
5. Push to your fork and create a Pull Request

### Commit Message Format
We use [Conventional Commits](https://conventionalcommits.org/):
- `feat:` - New features
- `fix:` - Bug fixes
- `docs:` - Documentation
- `style:` - Code style changes
- `refactor:` - Code refactoring
- `test:` - Adding tests
- `chore:` - Maintenance

## 📸 Screenshots

*Coming soon*

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Developer**: Sashank
- **Built with**: Paper API, Maven
- **Inspired by**: Modern Minecraft server plugins

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/sashu12-a11y/ProTPA/issues)
- **Discussions**: [GitHub Discussions](https://github.com/sashu12-a11y/ProTPA/discussions)
- **Email**: sashankc344@gmail.com

---
