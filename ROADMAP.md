# SimpJam Roadmap

Fork of [SimpMusic](https://github.com/maxrave-dev/SimpMusic) (GPL-3.0) — see FORK_NOTICE.md.
Goal: production-grade synchronized listen-together ("jam") experience with a self-hosted
sync relay, join links, and music-tuned sync.

## Phase 0 — Research & toolchain ✅
- 6-agent deep research: fork candidates, self-hosted ecosystems, sync tech, build system,
  metroserver deploy spec, jam backlog + GPL compliance (reports in `~/jam-research/`).
- Toolchain on ARM64 Ubuntu: JDK 21, Gradle 9.8.1, Android SDK (platform 37.0, ARM64 aapt2
  drop-in from Commit451/android-arm-build-tools), Go, cloudflared.
- Baseline clone of SimpMusic `dev` + `core` submodule.

## Phase 1 — Repo & baseline build ✅
- [x] New repo `kiru-ji/SimpJam` + core fork `kiru-ji/SimpJam-core` (branch `simpjam-dev`),
      GPL-3.0 §5a attribution (FORK_NOTICE.md).
- [x] JVM compile path green on ARM64; Android APK builds delegated to CI (x86_64 runners).
- [x] CI `simpjam-android.yml`: debug APK, listenTogether unit tests, release-r8-smoke
      (minified build DEX-grepped for wire-protocol classes), release-signed (zipalign +
      apksigner, keystore only in GitHub's vault, publishes to Releases on `v*` tags).
      All jobs verified green.

## Phase 2 — Self-hosted jam server ✅ (TLS pending operator action)
- [x] metroserver (GPL-3.0) built static linux/arm64; systemd unit, 127.0.0.1:8790,
      bbolt persistence (rooms survive restarts), UA policy, graceful shutdown, /health green.
- [x] Public wss via Cloudflare quick tunnel (interim; URL rotates on restart).
- [x] E2E proven against OUR relay: `SimpJamRoomE2ETest` — two real clients:
      create → join → approve → change_track → play → guest receives synced playback action.
- [ ] Permanent TLS: `deploy/Caddyfile.jam` validated (wss reverse proxy on sslip.io name);
      blocked ONLY by Oracle VCN ingress for 80/443 — console-side operator action.
- Server facts (verified from source): endpoints `/ws` (protobuf+gzip frames) and `/health`;
  env `PORT`, `UA_POLICY_FILE`, `DATABASE_FILE`, `UA_ADMIN_TOKEN`; rate limits (per-source
  32 conns, 10 room-creates/min, 60 msg/s), 512KB read limit, graceful shutdown with state save.
  ⚠️ Behind a plain reverse proxy all sources collapse to 127.0.0.1 unless `requestSource()`
  honors X-Real-IP (patch candidate) — Cloudflare tunnels pass CF-Connecting-IP, so OK today.

## Phase 3 — Jam production features (partially done)
- [x] **Join link + QR**: share sheet sends a real deep link
      (`https://simpmusic.org/app/lt?room=CODE`); QR button opens `InviteQrDialog`
      (expect/actual zxing renderer for android+jvm, hand-drawn SimpIcons.QrCode). Router
      handles `simpmusic://lt?room=`, new `simpjam://` scheme, and the https app-link;
      the code lands prefilled in the join field. Compiles green (JVM + CI).
- [x] **Blocklist enforced**: was stored+displayed but never read anywhere — a blocked guest
      could rejoin and auto-approve re-admitted them silently. Session now rejects a
      JOIN_REQUEST from a blocked name before the auto-approve path; VM mirrors the DataStore
      list live. Covered by BlocklistTest (5 tests, in CI).
- [x] **Reconnect asymmetry fixed (~29×)**: server holds the seat 15 min, client gave up in
      31 s (5 attempts). Now 9 attempts under the 60 s cap ≈ 4 min of retries.
- [x] **Sub-threshold drift correction** (Metrolist parity): 250 ms loop nudges guest speed
      ±2 % when drift is in (120 ms, 750 ms); hard gaps stay with the existing seek so the
      mechanisms never fight; user's own speed snapshotted and restored.
- [x] **Guest suggestTrack wired**: was plumbed session→repo with zero call sites; VM exposes it.
- [x] **R8 keeps** for `org.simpmusic.listentogether.**` (field names survive minification —
      the exact Metrolist 13.4.x failure).
- [x] **Guest "suggest this song" entry point**: NowPlayingBottomSheet gains a
      "Suggest to room" row (Groups icon, visible only while in a room) — builds a RoomTrack
      from the current song and calls the repository; disables after one send.
- [x] **Room queue UI + "Added by"**: `suggestedBy` now survives the domain boundary
      (RoomTrack field); every member sees the queue with position chips and attribution.
- [ ] Host toggles: "let others change playback / add songs".
- [ ] Persistent/named rooms (bbolt persistence exists now; #2571's original blocker is gone).
- [ ] Adopt the negotiated protocol features metroserver shipped (features, drift_report,
      host_sequence, track_revision, server_restarting) — no client opts in yet; SimpJam would
      be the first.
- [ ] Co-location "party mode" (single output, phones as remotes) — stretch.

## Phase 4 — Release
- [x] Signed release pipeline in CI (secrets in vault; verify step runs apksigner --verbose).
- [ ] Real-device 2-client verification (create/join/seek/pause/suggest/reconnect).
- [x] Cut `v0.1.0` tag → first published GitHub Release (4 signed APKs, v2 scheme verified;
      https://github.com/kiru-ji/SimpJam/releases/tag/v0.1.0).
- [ ] Docs: deploy guide (metroserver + Caddy + tunnel), threat model notes (blocklist is a
      convenience, room code + host approval are the real gates; no auth on the relay —
      keep it localhost-only behind the tunnel).

## Sync architecture reference (from research)
Option A model (same as Spotify Jam remote / Apple SharePlay / Jellyfin SyncPlay):
each client streams audio independently; a WebSocket room service syncs *state* only.
NTP-style 4-timestamp clock offset (min-delay filtered), future-scheduled commands
(executeAt = now + 2×maxPing), rate-trim→seek drift correction, buffer-barrier with
30s deadline escape, Apple-style suspensions (a phone call never pauses the room).
Full sourced synthesis: `~/jam-research/agent3_sync_tech.md`.
