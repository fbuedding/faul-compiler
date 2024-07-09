main:
# initializing s0 with address 0
move $s0, $0
addi $s0, $0, 1
# initializing s1 with address 4
move $s1, $0
addi $s1, $0, 2
# initializing s2 with address 8
move $s2, $0
addi $s2, $0, 3
# initializing s3 with address 12
move $s3, $0
addi $s3, $0, 4
# initializing s4 with address 16
move $s4, $0
addi $s4, $0, 5
# initializing s5 with address 20
move $s5, $0
addi $s5, $0, 6
# initializing s6 with address 24
move $s6, $0
addi $s6, $0, 7
# initializing s7 with address 28
move $s7, $0
addi $s7, $0, 8
# unloading s0 and storing in address 0
sw $s0, 0($gp)
# initializing s0 with address 32
move $s0, $0
addi $s0, $0, 9
# unloading s1 and storing in address 4
sw $s1, 4($gp)
# initializing s1 with address 36
move $s1, $0
addi $s1, $0, 10
# unloading s2 and storing in address 8
sw $s2, 8($gp)
# initializing s2 with address 40
move $s2, $0
addi $s2, $0, 11
# unloading s3 and storing in address 12
sw $s3, 12($gp)
# initializing s3 with address 44
move $s3, $0
addi $s3, $0, 12
# unloading s4 and storing in address 16
sw $s4, 16($gp)
# initializing s4 with address 48
move $s4, $0
add $t0, $s7, $s0
add $t0, $s6, $t0
add $t0, $s5, $t0
# unloading s1 and storing in address 36
sw $s1, 36($gp)
lw $s1, 16($gp)
add $t0, $s1, $t0
# unloading s2 and storing in address 40
sw $s2, 40($gp)
lw $s2, 12($gp)
add $t0, $s2, $t0
# unloading s3 and storing in address 44
sw $s3, 44($gp)
lw $s3, 8($gp)
add $t0, $s3, $t0
# unloading s4 and storing in address 48
sw $s4, 48($gp)
lw $s4, 4($gp)
add $t0, $s4, $t0
# unloading s7 and storing in address 28
sw $s7, 28($gp)
lw $s7, 0($gp)
add $t0, $s7, $t0
# unloading s0 and storing in address 32
sw $s0, 32($gp)
move $s0, $t0
# unloading all vars
# unloading s6 and storing in address 24
sw $s6, 24($gp)
# unloading s5 and storing in address 20
sw $s5, 20($gp)
# unloading s1 and storing in address 16
sw $s1, 16($gp)
# unloading s2 and storing in address 12
sw $s2, 12($gp)
# unloading s3 and storing in address 8
sw $s3, 8($gp)
# unloading s4 and storing in address 4
sw $s4, 4($gp)
# unloading s7 and storing in address 0
sw $s7, 0($gp)
# unloading s0 and storing in address 48
sw $s0, 48($gp)
# Exit
li $v0, 10
syscall
