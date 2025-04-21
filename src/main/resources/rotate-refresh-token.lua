-- rotate-refresh-token.lua
-- Atomic rotate of refresh token: generate new, revoke old, update per-user mapping
-- KEYS:
--   1) old refresh token key
--   2) new refresh token key
--   3) per-user mapping key
-- ARGV:
--   1) email
--   2) new raw token
--   3) ttl in seconds
if redis.call("EXISTS", KEYS[1]) == 0 then
  return nil
end
-- remove any existing token for this user
local oldUserTokenKey = redis.call("GET", KEYS[3])
if oldUserTokenKey then
  redis.call("DEL", oldUserTokenKey)
end
-- set new refresh token
redis.call("SET", KEYS[2], ARGV[1], "EX", tonumber(ARGV[3]))
-- update per-user mapping
redis.call("SET", KEYS[3], KEYS[2], "EX", tonumber(ARGV[3]))
-- delete old refresh token
redis.call("DEL", KEYS[1])
-- return the raw new token
return ARGV[2]