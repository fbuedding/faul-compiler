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
# unloading all vars
# unloading s4 and storing in address 16
sw $s4, 16($gp)
# unloading s5 and storing in address 20
sw $s5, 20($gp)
# unloading s6 and storing in address 24
sw $s6, 24($gp)
# unloading s7 and storing in address 28
sw $s7, 28($gp)
# unloading s0 and storing in address 32
sw $s0, 32($gp)
# unloading s1 and storing in address 36
sw $s1, 36($gp)
# unloading s2 and storing in address 40
sw $s2, 40($gp)
# unloading s3 and storing in address 44
sw $s3, 44($gp)
# Exit
li $v0, 10
syscall
