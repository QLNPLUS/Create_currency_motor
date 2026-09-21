# Currency Motor project rules

## Branch matrix

| Branch | Worktree path | Loader | Minecraft | JDK | Build plugin |
|---|---|---|---|---|---|
| forge-1.20.1 | D:\projects\Create_currency_motor | Forge | 1.20.1 | 17 | ForgeGradle 6.0.54 |
| neoforge-1.21.1 | D:\projects\Create_currency_motor\neoforge-1.21.1 | NeoForge | 1.21.1 | 21 | ModDevGradle 2.0.141 |

- The Forge worktree is the primary worktree and contains the shared Git metadata.
- This project intentionally has no common/ module because the loader and Minecraft API differences are substantial.
- Develop and verify one branch first, commit it, then propagate cross-version changes with git cherry-pick -x.
- Never hand-rewrite a cross-branch change before cherry-picking it. Resolve platform API differences explicitly after the cherry-pick.
- A user confirmation such as “tested”, “works”, or “sync to the other version” triggers propagation to the other applicable branch.

## Known platform gaps

- Forge 1.20.1 uses ForgeConfigSpec, Forge mod metadata, and Forge event registration.
- NeoForge 1.21.1 uses ModConfigSpec, NeoForge metadata, and NeoForge event registration.
- Q-shop wallet access is isolated behind a reflection bridge because its player data backend differs between loaders.
- Create's kinetic value behaviour and renderer APIs are shared conceptually but must be compiled against each target's Create artifact.

## Release and verification

- Release tags use v<version>-<loader>-<minecraft-version>.
- Use JDK 17 for Forge 1.20.1 and JDK 21 for NeoForge 1.21.1.
- Build each worktree serially with its own Gradle wrapper.
- Inspect the final JAR and report the output path and SHA-256.
