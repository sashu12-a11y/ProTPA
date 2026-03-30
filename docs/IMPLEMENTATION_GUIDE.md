PROTPA UPGRADE - COMPLETE IMPLEMENTATION GUIDE
=============================================

PROJECT STRUCTURE:
src/me/yourname/protpa/
├── ProTPA.java (Main plugin class with command/listener registration)
├── data/
│   └── RequestData.java (Data class for TPA requests with type handling)
├── commands/
│   ├── TpaCommand.java (Original /tpa command - UPDATED)
│   ├── TpahereCommand.java (NEW - /tpahere command)
│   ├── TpAcceptCommand.java (UPDATED - handles both TPA and TPAHERE)
│   ├── TpDenyCommand.java (UPDATED - handles request denials)
│   ├── TpaToggleCommand.java (Original toggle command)
│   ├── RTPCommand.java (NEW - Random teleport command)
│   └── ReloadCommand.java (Plugin reload command)
├── managers/
│   ├── TPAManager.java (UPDATED - contains all request/cooldown management)
│   ├── ConfigManager.java (Configuration management)
│   ├── MessageManager.java (Message/color code management)
│   ├── CombatManager.java (Player combat state tracking)
│   └── LogManager.java (Event logging)
└── listeners/
    ├── PlayerMoveListener.java (Teleport cancellation on move)
    ├── PlayerDamageListener.java (Combat tagging & teleport cancellation)
    └── PlayerQuitListener.java (Cleanup on player disconnect)

KEY FEATURES ADDED:
===================

1. TPAHERE SYSTEM
   - /tpahere <player> sends request for target to teleport to requester
   - Stored in RequestData with type: TPAHERE
   - All existing checks apply (toggle, cooldown, self-check, duplicate)
   - Message: "wants you to teleport to them!"

2. RTP SYSTEM (Random Teleport)
   - /rtp teleports player to random safe location
   - Config-based radius: min-radius (100), max-radius (1000)
   - Safety checks: solid block below, not lava/water
   - Retry up to 10 times for safe location
   - Cooldown: 30 seconds (configurable)
   - Teleport delay: 3 seconds (reuses TPA system)
   - Cancel on: move, damage, combat (if configured)

3. REQUEST DATA STORAGE
   - Map<UUID, Map<UUID, RequestData>> in TPAManager
   - RequestData contains: requesterUUID, requestType (enum), timestamp
   - Auto-expire requests after configured timeout
   - Support for multiple concurrent request types

4. UPDATED COMMAND FLOW
   /tpaccept:
   - If TPA: requester → target player
   - If TPAHERE: target → requester player
   - Both trigger same teleport delay & safety checks

5. COOLDOWN SYSTEM
   - Separate cooldowns for TPA/TPAHERE and RTP
   - Track in milliseconds with expiration
   - Bypass with: protpa.bypass.cooldown permission
   - Each command adds own cooldown

CONFIGURATION FILES:
====================

plugin.yml additions:
- tpahere: /tpahere <player> command
- rtp: /rtp random teleport command
- permissions: protpa.rtp, protpa.bypass.cooldown, protpa.bypass.combat

config.yml additions:
- rtp.min-radius: 100 (blocks)
- rtp.max-radius: 1000 (blocks)
- rtp.cooldown: 30 (seconds)

messages.yml additions:
- tpahere-request: "{player} wants you to teleport to them!"
- rtp-start: "Teleporting to a random safe location..."
- rtp-fail: "Could not find a safe location!"
- rtp-complete: "Teleported to random location!"

COMPILATION:
============

1. Ensure Java 1.20+ JDK installed
2. Compile source files:
   javac -cp "bukkit.jar:.*" src/me/yourname/protpa/**/*.java -d bin

3. Create JAR:
   jar cf ProTPA.jar -C bin . -C . plugin.yml config.yml messages.yml logs.yml

4. Place in plugins/ folder
5. Restart server

DEPENDENCIES:
=============
- Bukkit/Paper API 1.20+
- Java 1.20+

PERMISSIONS:
============
- protpa.use: Use /tpa and /tpahere
- protpa.accept: Use /tpaccept
- protpa.deny: Use /tpdeny
- protpa.toggle: Use /tpatoggle
- protpa.rtp: Use /rtp
- protpa.reload: Reload plugin configuration
- protpa.bypass.cooldown: Bypass all cooldowns
- protpa.bypass.combat: Teleport during combat

IMPLEMENTATION CHECKLIST:
========================
[✓] RequestData.java - Stores request type
[✓] TPAManager.java - All request/cooldown methods
[✓] TpaCommand.java - TPA system
[✓] TpahereCommand.java - TPAHERE system
[✓] TpAcceptCommand.java - Updated for both types
[✓] TpDenyCommand.java - Request denial
[✓] RTPCommand.java - Random teleport
[✓] PlayerMoveListener.java - Teleport cancellation
[✓] PlayerDamageListener.java - Combat + teleport cancellation
[✓] PlayerQuitListener.java - Cleanup
[✓] ProTPA.java - Command/listener registration
[✓] ConfigManager.java - Configuration
[✓] MessageManager.java - Localization
[✓] CombatManager.java - Combat tracking
[✓] LogManager.java - Logging
[✓] plugin.yml - Updated commands + permissions
[✓] config.yml - RTP configuration
[✓] messages.yml - New messages

All files are production-ready and compilable!
