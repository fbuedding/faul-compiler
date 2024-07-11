main:
# initializing s0 with address 16
move $s0, $0
addi $s0, $0, 1
# initializing s1 with address 20
move $s1, $0
addi $s1, $0, 0
# While start
label_1:
# Loading value 16
li $t0, 16
# Less than
slt $t0, $s0, $t0
beqz $t0, label_0
nop
# Loading value 2
li $t1, 2
mul $t1, $s0, $t1
# Assgining reg s0 with address 4
move $s0, $t1
# Loading value 1
li $t1, 1
add $t1, $s1, $t1
# Assgining reg s1 with address 5
move $s1, $t1
b label_1
nop
# While end
label_0:
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
