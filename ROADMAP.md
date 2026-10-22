# SimpJam Roadmap

Fork of [SimpMusic](https://github.com/maxrave-dev/SimpMusic) (GPL-3.0) — see FORK_NOTICE.md.
Goal: production-grade synchronized listen-together ("jam") experience with a self-hosted
sync relay, join links, and music-tuned sync.

## Phase 0 — Research & toolchain ✅
- 3-agent deep research: build system, metroserver deploy spec, jam feature backlog
  (reports in `~/jam-research/` on the build VPS).
- Toolchain on ARM64 Ubuntu: JDK 21, Gradle 9.8.1, Android SDK (platform 37.0,
  build-tools 36), Go 1.27.3.
- Baseline clone of SimpMusic `dev` (c37e436) + `core` submodule (f1ff657).

## Phase 1 — Repo & baseline build (in progress)
- [x] New repo `kiru-ji/SimpJam` with GPL-3.0 §5a attribution (FORK_NOTICE.md).
- [ ] Baseline Android debug APK compiles on ARM64 (verification gate).
- [ ] Desktop JVM build path documented.

## Phase 2 — Self-hosted jam server
- [x] metroserver (MetrolistGroup/metroserver, GPL-3.0) built static for linux/arm64.
- [ ] systemd unit on VPS, port 8790 (localhost-only), bbolt state persistence.
- [ ] wss reverse proxy (Caddy/nginx) + Let's Encrypt TLS on jam.<domain>.
- [ ] Point fork's Listen Together server setting at self-hosted wss URL; E2E room test.
- Server facts (verified from source): endpoints `/ws` (protobuf+gzip frames) and
  `/health`; env `PORT`, `UA_POLICY_FILE`, `DATABASE_FILE`, `UA_ADMIN_TOKEN`;
  built-in rate limiting (message window), 512KB read limit, graceful shutdown with
  state save; bbolt persistence → rooms survive restarts.

## Phase 3 — Jam production features
- [ ] Join links + QR codes (room deep-link → join flow).
- [ ] Music-tuned sync thresholds (tighter deadband; per-user output-latency offset).
- [ ] Host controls hardening: real ban primitive (id-based, not name-based blocklist).
- [ ] Host transfer UX polish (protocol primitive exists).
- [ ] Reconnect resilience review (session-token resume path).
- [ ] Co-location "party mode" (single output, phones as remotes) — stretch.

## Phase 4 — Release
- [ ] Signed release build + reproducible CI (GitHub Actions).
- [ ] E2E verification: 2-client room on real devices (create/join/seek/pause/chat/reconnect).
- [ ] Docs: deploy guide, threat model notes, license compliance.

## Sync architecture reference (from research)
Option A model (same as Spotify Jam remote / Apple SharePlay / Jellyfin SyncPlay):
each client streams audio independently; a WebSocket room service syncs *state* only.
NTP-style 4-timestamp clock offset (min-delay filtered), future-scheduled commands
(executeAt = now + 2×maxPing), rate-trim→seek drift correction, buffer-barrier with
30s deadline escape, Apple-style suspensions (a phone call never pauses the room).
Full sourced synthesis: `~/jam-research/agent3_sync_tech.md`.
