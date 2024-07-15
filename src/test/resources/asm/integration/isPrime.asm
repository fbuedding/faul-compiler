main:
# initializing s0 with address 16
move $s0, $0
jal readI
nop
# Assgining reg s0 with address 4
move $s0, $v0
# If start
# Loading value 1
li $t0, 1
# Less than equals
sle $t0, $s0, $t0
beqz $t0, label_0
nop
# Loading value 0
li $t1, 0
move $a0, $t1
jal print
nop
jal exit
nop
# If end
label_0:
# initializing s1 with address 20
move $s1, $0
addi $s1, $0, 2
# While start
label_2:
# Less than
slt $t1, $s1, $s0
beqz $t1, label_1
nop
# If start
# Modulo
div $s0, $s1
mfhi $t2
# Loading value 0
li $t3, 0
# Equals
seq $t2, $t2, $t3
beqz $t2, label_3
nop
# Loading value 0
li $t3, 0
move $a0, $t3
jal print
nop
jal exit
nop
# If end
label_3:
# Loading value 1
li $t3, 1
add $t3, $s1, $t3
# Assgining reg s1 with address 5
move $s1, $t3
b label_2
nop
# While end
label_1:
# Loading value 1
li $t3, 1
move $a0, $t3
jal print
nop
# unloading all vars
# unloading s0 and storing in address 16
sw $s0, 16($gp)
# unloading s1 and storing in address 20
sw $s1, 20($gp)
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
