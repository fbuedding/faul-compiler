main:
# initializing s0 from address 0
move $s0, $0
addi $t0, $0, 1
move $s0, $t0
# initializing s1 from address 4
move $s1, $0
addi $t0, $0, 2
move $s1, $t0
# initializing s2 from address 8
move $s2, $0
addi $t0, $0, 3
move $s2, $t0
# initializing s3 from address 12
move $s3, $0
addi $t0, $0, 4
move $s3, $t0
# initializing s4 from address 16
move $s4, $0
addi $t0, $0, 5
move $s4, $t0
# initializing s5 from address 20
move $s5, $0
addi $t0, $0, 6
move $s5, $t0
# initializing s6 from address 24
move $s6, $0
addi $t0, $0, 7
move $s6, $t0
# initializing s7 from address 28
move $s7, $0
addi $t0, $0, 8
move $s7, $t0
# unloading s0 and storing in address 0
sw $s0, 0($gp)
# initializing s0 from address 32
move $s0, $0
addi $t0, $0, 9
move $s0, $t0
# unloading s1 and storing in address 4
sw $s1, 4($gp)
# initializing s1 from address 36
move $s1, $0
addi $t0, $0, 10
move $s1, $t0
# unloading s2 and storing in address 8
sw $s2, 8($gp)
# initializing s2 from address 40
move $s2, $0
addi $t0, $0, 11
move $s2, $t0
# unloading s3 and storing in address 12
sw $s3, 12($gp)
# initializing s3 from address 44
move $s3, $0
addi $t0, $0, 12
move $s3, $t0
