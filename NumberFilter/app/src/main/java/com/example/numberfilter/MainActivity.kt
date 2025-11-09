package com.example.numberfilter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.numberfilter.databinding.ActivityMainBinding
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter = NumberAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Danh sách
        binding.rvNumbers.layoutManager = LinearLayoutManager(this)
        binding.rvNumbers.adapter = adapter

        // Chọn mặc định là Số lẻ như ảnh minh họa
        binding.rbOdd.isChecked = true

        // Lắng nghe thay đổi nhập liệu
        binding.edtLimit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updateList() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Lắng nghe thay đổi loại số (RadioGroup đảm bảo CHỈ 1 lựa chọn)
        binding.rgTypes.setOnCheckedChangeListener { _, checkedId ->
            Log.d("MainActivity", "RadioGroup changed to ID: $checkedId")
            updateList()
        }

        // Lần đầu hiển thị (nếu có placeholder)
        updateList()
    }

    // ---- Tạo danh sách theo N và loại đang chọn ----
    private fun updateList() {
        val text = binding.edtLimit.text?.toString()?.trim().orEmpty()

        // Nếu không nhập gì hoặc không phải số hợp lệ, ẩn cả danh sách và message
        if (text.isEmpty()) {
            hideAll()
            return
        }

        val n = text.toIntOrNull() ?: 0
        if (n <= 0) {
            hideAll()
            return
        }

        val selectedType = checkedType()
        Log.d("MainActivity", "updateList: n=$n, type=$selectedType")

        val list = buildList(n) { num ->
            when (selectedType) {
                Type.ODD -> num % 2 != 0
                Type.EVEN -> num % 2 == 0
                Type.PRIME -> isPrime(num)
                Type.SQUARE -> isSquare(num)
                Type.PERFECT -> isPerfect(num)
                Type.FIBO -> isFibonacci(num)
            }
        }

        Log.d("MainActivity", "List size: ${list.size}")
        if (list.isEmpty()) showEmpty() else showList(list)
    }

    private fun buildList(limit: Int, keep: (Int) -> Boolean): List<Int> {
        val out = ArrayList<Int>()
        for (i in 1 until limit) { // < N
            if (keep(i)) out.add(i)
        }
        return out
    }

    private fun checkedType(): Type = when (binding.rgTypes.checkedRadioButtonId) {
        binding.rbOdd.id -> Type.ODD
        binding.rbEven.id -> Type.EVEN
        binding.rbPrime.id -> Type.PRIME
        binding.rbSquare.id -> Type.SQUARE
        binding.rbPerfect.id -> Type.PERFECT
        binding.rbFibo.id -> Type.FIBO
        else -> Type.ODD
    }

    private fun hideAll() {
        adapter.submit(emptyList())
        binding.tvEmpty.visibility = android.view.View.GONE
        binding.rvNumbers.visibility = android.view.View.GONE
    }

    private fun showEmpty() {
        adapter.submit(emptyList())
        binding.tvEmpty.visibility = android.view.View.VISIBLE
        binding.rvNumbers.visibility = android.view.View.GONE
    }

    private fun showList(list: List<Int>) {
        adapter.submit(list)
        binding.tvEmpty.visibility = android.view.View.GONE
        binding.rvNumbers.visibility = android.view.View.VISIBLE
    }

    // ---- Các hàm kiểm tra số ----

    // Số nguyên tố: thử đến sqrt(n)
    private fun isPrime(x: Int): Boolean {
        if (x < 2) return false
        if (x % 2 == 0) return x == 2
        var i = 3
        val r = sqrt(x.toDouble()).toInt()
        while (i <= r) {
            if (x % i == 0) return false
            i += 2
        }
        return true
    }

    // Số chính phương
    private fun isSquare(x: Int): Boolean {
        val r = sqrt(x.toDouble()).toInt()
        return r * r == x
    }

    // Số hoàn hảo: tổng ước số dương (không tính chính nó) == x
    private fun isPerfect(x: Int): Boolean {
        if (x < 2) return false
        var sum = 1
        var i = 2
        val r = sqrt(x.toDouble()).toInt()
        while (i <= r) {
            if (x % i == 0) {
                sum += i
                val j = x / i
                if (j != i) sum += j
            }
            i++
        }
        return sum == x
    }

    // Số Fibonacci: 5n^2 ± 4 là chính phương
    private fun isFibonacci(x: Int): Boolean {
        if (x < 0) return false
        val a = 5L * x * x + 4
        val b = 5L * x * x - 4
        return isPerfectSquareLong(a) || isPerfectSquareLong(b)
    }

    private fun isPerfectSquareLong(v: Long): Boolean {
        val r = sqrt(v.toDouble()).toLong()
        return r * r == v
    }

    enum class Type { ODD, EVEN, PRIME, SQUARE, PERFECT, FIBO }
}
