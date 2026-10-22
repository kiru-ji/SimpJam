# SimpJam self-hosted relay (metroserver)

Production deployment of [MetrolistGroup/metroserver](https://github.com/MetrolistGroup/metroserver)
(GPL-3.0) — the Listen Together sync relay.

## Build (any Linux; static, CGO-free)
```bash
git clone --recursive https://github.com/MetrolistGroup/metroserver
cd metroserver
sudo apt-get install -y protobuf-compiler
go install google.golang.org/protobuf/cmd/protoc-gen-go@v1.36.11
bash scripts/generate_proto.sh
CGO_ENABLED=0 GOOS=linux GOARCH=arm64 go build -o metroserver -ldflags="-w -s" ./cmd/metroserver
```

## Deploy
```bash
sudo mkdir -p /opt/metroserver /var/lib/metroserver
sudo cp metroserver /opt/metroserver/
sudo cp ua_policy.example.json /opt/metroserver/ua_policy.json
sudo cp metroserver.service /etc/systemd/system/
sudo systemctl daemon-reload && sudo systemctl enable --now metroserver
curl -s http://127.0.0.1:8790/health   # {"status":"ok"}
```

Env: `PORT` (8790), `UA_POLICY_FILE`, `DATABASE_FILE` (bbolt — rooms survive restarts),
`UA_ADMIN_TOKEN` (optional /uas admin endpoint). Default UA tier is **allow**.

## Public TLS endpoint (no domain needed, dev/staging)
```bash
cloudflared tunnel --no-autoupdate --url http://127.0.0.1:8790
# → wss://<random>.trycloudflare.com/ws   (URL is EPHEMERAL — changes per run)
```
For production: named tunnel with fixed hostname, or Caddy + a real domain:
```
jam.example.com {
    reverse_proxy 127.0.0.1:8790
}
```
(Caddy handles wss upgrade + Let's Encrypt automatically.)

## Client config
SimpMusic/SimpJam reads the server URL from DataStore key `lt_server_url`
(ListenTogetherPrefs.SERVER_URL); blank falls back to the community server.
Set it to `wss://<your-host>/ws`.

## Verified 2026-10-22
- systemd unit active on ARM64 Ubuntu 22.04, /health OK localhost + via tunnel.
- Two-client E2E (JVM, listenTogether module): handshake (server v2, gzip+protobuf),
  create_room, join_room, approve_join, change_track, play → guest received synced
  PLAYBACK_ACTION. See SimpJamRoomE2ETest.
