package com

import java.util.*
import kotlin.random.Random


data class Member(val name: String, // 이름
                  val value: Int,   // 기여도
                  var chance: Double = 0.0, // 확률
                  var pickedCount: Int = 0 // 당첨 횟수
)


class TranscendCrystalDistributor {

    // Input
    private val rawInput = "황금나사 675\n" +
            "백은나사 595\n" +
            "얼음나사 500\n" +
            "투명나사 700\n" +
            "초합금나사 200"

    // 멤버와 기여도 목록
    private var memberList: Array<Member> = arrayOf()

    // 시도 횟수
    private var tryCount = 0
    private val totalCount = 10


    constructor() {

        println("--- 초월 수정 뽑기를 시작합니다! ---")
        println("- 진행 날짜 : " + getCurrentTimeString())
        println("--- 각 확률은 3번째 자리에서 반올림되어 총 합이 100%가 아닐 수 있음 ---")
        println()

    }

    private fun getCurrentTimeString() : String {
        val cal = Calendar.getInstance()
        return "" + cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH) + 1) + "월 " + cal.get(Calendar.DAY_OF_MONTH) + "일 " +
                cal.get(Calendar.HOUR_OF_DAY) + "시 " + cal.get(Calendar.MINUTE) + "분 " + cal.get(Calendar.SECOND) + "초"
    }


    fun read() {

        // 읽기
        memberList = rawInput.replace("\r\n", "|").replace("\n", "|").split("|").map {
            val comp = it.split(" ")
            Member(comp[0], comp[1].toInt())
        }.sortedByDescending { it.value }.toTypedArray()

        // 읽은 값 출력
        println("--- 각 멤버별 기여도 ---")
        memberList.forEach {
            println("- " + it.name + " : " + it.value)
        }
        println()

    }

    fun makeTable() {

        println("--- 초월 수정 뽑기 확률표 ---")

        // 확률 계산 및 출력
        val valueSum = memberList.sumBy { it.value }
        memberList.forEach {
            it.chance = it.value.toDouble() / valueSum
            println("- " + it.name + " : " + String.format("%.2f%%", it.chance * 100))
        }

        println()

    }


    fun electUntilEnd() {

        println("--- 뽑기 시작 ---")
        println()

        for(i in 0 until totalCount) {
            Thread.sleep(100) // 시드 갱신용
            elect()
        }

        println("--- 뽑기 종료 ---")
        println("--- 당첨자 목록 ---")

        val pickedList = memberList.filter { it.pickedCount > 0 }.sortedBy { it.pickedCount }
        pickedList.forEach {
            println("- " + it.name + " : " + it.pickedCount + "개")
        }

    }

    private fun elect() {

        tryCount++
        println("--- " + tryCount + "번째 뽑기 ---")

        val seed = System.currentTimeMillis()
        println("Seed : $seed")

        val random = Random(seed)
        val randomValue = random.nextDouble()
        println("랜덤 값 : $randomValue")

        var cursor = 0.0
        for(member in memberList) {
            if(cursor + member.chance > randomValue) {
                member.pickedCount++
                println("당첨 : " + member.name)
                break
            } else {
                cursor += member.chance
            }
        }

        println()

    }







}

fun main() {

    val distributor = TranscendCrystalDistributor()
    distributor.read()
    distributor.makeTable()
    distributor.electUntilEnd()

}
