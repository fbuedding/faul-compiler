main:
# initializing s0 with address 16
move $s0, $0
# Loading value 8
li $t0, 8
# Loading value 4
li $t1, 4
div $t0, $t0, $t1
# Loading value 3
li $t1, 3
mul $t0, $t0, $t1
# Assgining reg s0 with address 4
move $s0, $t0
# initializing s1 with address 20
move $s1, $0
# negating $s
sub $t0, $0, $s0
mul $t0, $s0, $t0
# Assgining reg s1 with address 5
move $s1, $t0
# initializing s2 with address 24
move $s2, $0
# Loading value 5
li $t0, 5
# Modulo
div $s1, $t0
mfhi $t0
# Assgining reg s2 with address 6
move $s2, $t0
# initializing s3 with address 28
move $s3, $0
# Loading value 1
li $t0, 1
nor $t0, $t0, $t0
andi $t0, $t0, 1
# Assgining reg s3 with address 7
move $s3, $t0
# initializing s4 with address 32
move $s4, $0
addi $s4, $0, 0
# If start
# Loading value 1
li $t0, 1
beqz $t0, label_1
nop
# initializing s5 with address 36
move $s5, $0
addi $s5, $0, 0
addi $s4, $0, 1
b label_0
nop
# else start
label_1:
addi $s4, $0, 2
# If end
label_0:
# initializing s6 with address 36
move $s6, $0
addi $s6, $0, 2
# unloading all vars
# unloading s0 and storing in address 16
sw $s0, 16($gp)
# unloading s1 and storing in address 20
sw $s1, 20($gp)
# unloading s2 and storing in address 24
sw $s2, 24($gp)
# unloading s3 and storing in address 28
sw $s3, 28($gp)
# unloading s4 and storing in address 32
sw $s4, 32($gp)
# unloading s6 and storing in address 36
sw $s6, 36($gp)
# Exit
li $v0, 10
syscall
readI:
addi $sp, $sp, -4
sw $ra, 0($sp)
li $v0, 5
syscall
nop
lw $ra, 0($sp)
addi $sp, $sp, 4
jr $ra
nop
readB:
addi $sp, $sp, -4
sw $ra, 0($sp)
li $v0, 5
syscall
nop
andi $v0, $v0, 1
lw $ra, 0($sp)
addi $sp, $sp, 4
jr $ra
nop
print:
addi $sp, $sp, -4
sw $ra, 0($sp)
li $v0, 1
syscall
nop
lw $ra, 0($sp)
addi $sp, $sp, 4
jr $ra
nop
exit:
# Exit
li $v0, 10
syscall
