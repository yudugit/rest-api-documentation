#!/usr/bin/env ruby

require 'openssl'
require 'base64'

string_to_sign = ARGV[0]
shared_secret = ARGV[1]

unless string_to_sign && shared_secret
  raise 'Requires two arguments: String to sign & shared secret.'
end

puts "Signing string '#{string_to_sign}' with secret '#{shared_secret}'"

sha256 = OpenSSL::Digest::SHA256.new
digest = OpenSSL::HMAC.digest(sha256, shared_secret, string_to_sign)

puts Base64.encode64(digest).chomp
