# Joist Take-Home Assessment

An Android app that submits text to a simulated validation endpoint and displays the result. Built as a take-home exercise.

## Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| State | ViewModel + StateFlow |
| DI | Koin 4.1 |
| Navigation | Navigation Compose |
| Unit tests | JUnit 4 + MockK + Turbine |
| UI tests | Compose Test Rule + Espresso |

- **Min SDK:** 24 · **Target SDK:** 36
- **Kotlin:** 2.3 · **AGP:** 9.2

## Getting started

1. Clone the repo.
2. Open in Android Studio Ladybug (or newer).
3. Run the `app` configuration on an emulator or device (API 24+).

No API keys or local configuration are required.

## Running tests

```bash
# Unit tests
./gradlew test

# Instrumented / UI tests (emulator or device required)
./gradlew connectedAndroidTest
```

## Project structure

```
app/src/main/java/com/joist/assestment/
├── data/          # ValidationRepository interface + impl
├── di/            # Koin module
├── navigation/    # NavGraph + Routes
├── ui/            # EchoScreen, EchoViewModel, OutputCard, Theme
└── App.kt / MainActivity.kt
```

## Architecture

The app follows **MVVM** with a single `EchoViewModel` that owns all business logic. Composables are pure render functions that react to state — no logic leaks into the UI layer.

```
EchoScreen  ──►  EchoViewModel  ──►  ValidationRepository
    ▲                  │
    └──── EchoUiState ◄┘  (StateFlow)
```

`EchoUiState` is a **sealed interface** with four states:

| State | Description |
|---|---|
| `Idle` | Initial / reset state |
| `Loading` | Request in flight |
| `Success` | Server accepted the input |
| `Error` | Server rejected the input |

## Simulated validation rules

- The repository introduces a **1 500 ms delay** to mimic a network round-trip.
- Any input that starts with the word **"error"** (case-insensitive) is treated as a server rejection, giving a deterministic failure path without randomness.

## Technical decisions

### StateFlow over LiveData
`StateFlow` is Kotlin-first, integrates naturally with coroutines, and does not require a lifecycle owner — `collectAsState()` handles that automatically in Compose.

### Koin over Hilt
Koin has no annotation-processing step, which keeps build times fast for a project of this size. `koinViewModel()` gives clean ViewModel injection in Compose with minimal boilerplate.

### Unit test design
Tests use `StandardTestDispatcher` + `Dispatchers.setMain` so `viewModelScope` coroutines run under the test scheduler. **Turbine** asserts the full emission sequence (`Idle → Loading → Success/Error`) without polling or arbitrary sleeps.

### UI test design
`createComposeRule` with the ViewModel injected directly (bypassing Koin) keeps each test isolated. `waitUntil` handles async state transitions deterministically.

### Deliberate omissions

- **No navigation library** — a single screen needs no router (Navigation Compose is wired up but the graph has one destination).
- **No Room / network client** — the task calls for a simulated call, not a real one.
- **No Hilt** — Koin is sufficient and avoids annotation-processing overhead here.
