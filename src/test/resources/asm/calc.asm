main:
# initializing s0 with address 16
		move $s0, $0
		addi $s0, $0, 0
# initializing s1 with address 20
		move $s1, $0
		addi $s1, $0, 0
# initializing s2 with address 24
		move $s2, $0
		addi $s2, $0, 0
# While start
label_1:
		jal readB
		nop
		beqz $v0, label_0
		nop
# initializing s3 with address 28
		move $s3, $0
		jal readI
		nop
# Assgining reg s3 with address 7
		move $s3, $v0
		jal readI
		nop
# Assgining reg s0 with address 4
		move $s0, $v0
		jal readI
		nop
# Assgining reg s1 with address 5
		move $s1, $v0
# If start
# Loading value 0
		li $t0, 0
# Equals
		seq $t0, $s3, $t0
		beqz $t0, label_2
		nop
		add $t1, $s0, $s1
# Assgining reg s2 with address 6
		move $s2, $t1
# If end
label_2:
# If start
# Loading value 1
		li $t1, 1
# Equals
		seq $t1, $s3, $t1
		beqz $t1, label_3
		nop
		sub $t2, $s0, $s1
# Assgining reg s2 with address 6
		move $s2, $t2
# If end
label_3:
# If start
# Loading value 2
		li $t2, 2
# Equals
		seq $t2, $s3, $t2
		beqz $t2, label_4
		nop
		mul $t3, $s0, $s1
# Assgining reg s2 with address 6
		move $s2, $t3
# If end
label_4:
# If start
# Loading value 4
		li $t3, 4
# Equals
		seq $t3, $s3, $t3
		beqz $t3, label_5
		nop
# If start
# Loading value 0
		li $t4, 0
# Equals
		seq $t4, $s0, $t4
		beqz $t4, label_6
		nop
		jal exit
		nop
# If end
label_6:
		div $t5, $s0, $s1
# Assgining reg s2 with address 6
		move $s2, $t5
# If end
label_5:
# If start
# Loading value 5
		li $t5, 5
# Equals
		seq $t5, $s3, $t5
		beqz $t5, label_7
		nop
# If start
# Loading value 0
		li $t6, 0
# Equals
		seq $t6, $s0, $t6
		beqz $t6, label_8
		nop
		jal exit
		nop
# If end
label_8:
# Modulo
		div $s0, $s1
		mfhi $t7
# Assgining reg s2 with address 6
		move $s2, $t7
# If end
label_7:
		move $a0, $s2
		jal print
		nop
		b label_1
		nop
# While end
label_0:
		jal exit
		nop
# unloading all vars
# unloading s3 and storing in address 28
		sw $s3, 28($gp)
# unloading s0 and storing in address 16
		sw $s0, 16($gp)
# unloading s1 and storing in address 20
		sw $s1, 20($gp)
# unloading s2 and storing in address 24
		sw $s2, 24($gp)
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
