# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [5.0.0] - 2026-05-30

### Added
- Created `SensorRepository` exposing sensor streams via Kotlin Flows.
- Created `AudioPlayer` encapsulating `SoundPool` asset lifecycle and playbacks.
- Created `TambourineViewModel` implementing clean MVVM architectural decoupling.
- Custom dark-theme styling support in `Theme.kt` and `Color.kt`.

### Changed
- Refactored entire lifecycle to register sensor listener in `onResume` and unregister in `onPause` to prevent memory leaks and battery drain.
- Upgraded shake detection math from 1D X-axis to robust 3D vector magnitude evaluation.
- Added software high-pass gravity filtering to prevent device orientation triggers.
- Replaced direct raw sensor data binding in UI with a low-pass filter to smooth tambourine rotation.
- SoundPool loaded validations now enforce loading completion of all assets to prevent early-tap sound failures.

## [4.0.0] - Legacy
- Initial release featuring accelerometer-based tambourine shaking and click-to-play functionality.
