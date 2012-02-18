def morton_code_for(bits)
  method = ''

  limit_mask = (1 << (bits * 3)) - 1
  split = (2 ** ((Math.log(bits) / Math.log(2)).to_i + 1)).to_i
  level = 1

  puts "// Coding for 3 #{bits}-bit values"

  loop do
    shift = split
    split /= 2
    level *= 2

    mask = ([ '1' * split ] * level).join('0' * split * 2).to_i(2) & limit_mask

    expression = "v = (v | (v << %2d)) & 0x%016x;" % [ shift, mask ]

    method << expression

    puts "%s // 0b%064b" % [ expression, mask ]

    break if (split <= 1)
  end

  puts
  print "// Test of method results: "
  v = (1 << bits) - 1
  puts eval(method).to_s(2)
end

morton_code_for(19)