# Create Currency Motor

Create Currency Motor adds a Create kinetic generator that consumes the owning player's Q-shop currency while running. Place the motor, set its speed, and let it generate rotational power as long as the owner can pay the configured charge.

## Features

- Forge 1.20.1 support.
- NeoForge 1.21.1 support.
- Create kinetic power generation up to 256 RPM.
- Per-RPM currency cost with decimal configuration from `0.1` to `1000000000000`.
- Final payments are rounded up to the next whole currency unit.
- Player avatar display on the motor owner slot.
- Owner-only avatar slot interaction.
- Configurable charge interval, stress capacity, and Q-Shop currency id.

## Requirements

- Minecraft 1.20.1 with Forge, or Minecraft 1.21.1 with NeoForge.
- Create for the matching Minecraft version.
- Q-Shop 1.8.1 or newer.

## Configuration

The common configuration file is `config/create_currency_motor-common.toml`.

```toml
[create_currency_motor]
# Currency charged per RPM on each payment interval.
# Range: 0.1 ~ 1000000000000
currency_per_rpm = 1.0

# Stress capacity at 256 RPM.
max_stress = 16384.0

# Payment interval in ticks.
# Range: 1 ~ 100
charge_interval_ticks = 20

# Q-Shop currency id.
currency_id = "coins"
```

For example, at 16 RPM and `currency_per_rpm = 0.1`, the raw charge is 1.6 and the motor withdraws 2 whole currency units.

## License

MIT
