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
		move $a0, $s0
		jal print
		nop
		jal exit
		nop
# If end
label_0:
# initializing s1 with address 20
		move $s1, $0
		addi $s1, $0, 1
# initializing s2 with address 24
		move $s2, $0
		addi $s2, $0, 1
# initializing s3 with address 28
		move $s3, $0
		addi $s3, $0, 2
# While start
label_2:
# Less than
		slt $t1, $s3, $s0
		beqz $t1, label_1
		nop
# initializing s4 with address 32
		move $s4, $0
# Assgining reg s4 with address 8
		move $s4, $s1
		add $t2, $s1, $s2
# Assgining reg s1 with address 5
		move $s1, $t2
# Assgining reg s2 with address 6
		move $s2, $s4
# Loading value 1
		li $t2, 1
		add $t2, $s3, $t2
# Assgining reg s3 with address 7
		move $s3, $t2
		b label_2
		nop
# While end
label_1:
		move $a0, $s1
		jal print
		nop
# unloading all vars
# unloading s0 and storing in address 16
		sw $s0, 16($gp)
# unloading s4 and storing in address 32
		sw $s4, 32($gp)
# unloading s2 and storing in address 24
		sw $s2, 24($gp)
# unloading s3 and storing in address 28
		sw $s3, 28($gp)
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
