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
li $v0, 16
# Less than
slt $t0, $s0, $v0
beqz $t0, label_0
nop
# Loading value 2
li $v0, 2
mul $t1, $s0, $v0
move $s0, $t1
# Loading value 1
li $v0, 1
add $t1, $s1, $v0
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
