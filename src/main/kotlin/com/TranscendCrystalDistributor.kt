package com

import java.util.*
import kotlin.random.Random


data class Member(val name: String, // 이름
                  val value: Int,   // 기여도
                  var chance: Double = 0.0, // 확률
                  var highChance: Double = 0.0, // 높은 기여도 확률
                  var pickedCount: Int = 0 // 당첨 횟수
)


class TranscendCrystalDistributor {

    companion object {

        // 멤버와 기여도
        private const val INPUT = "길드원1 720\n" +
                "길드원2 625\n" +
                "길드원3 505\n" +
                "길드원4 475\n" +
                "길드원5 420\n" +
                "길드원6 350\n" +
                "길드원7 300\n" +
                "길드원8 245\n" +
                "길드원9 240\n" +
                "길드원10 220\n" +
                "길드원11 220\n" +
                "길드원12 215\n" +
                "길드원13 215\n" +
                "길드원14 205\n" +
                "길드원15 114\n" +
                "길드원16 100\n" +
                "길드원17 605\n" +
                "길드원18 585\n" +
                "길드원19 550\n" +
                "길드원20 550\n" +
                "길드원21 485\n" +
                "길드원22 420\n" +
                "길드원23 420\n" +
                "길드원24 420\n" +
                "길드원25 320\n" +
                "길드원26 300\n" +
                "길드원27 266\n" +
                "길드원28 245\n" +
                "길드원29 240\n" +
                "길드원30 180\n" +
                "길드원31 160\n" +
                "길드원32 120\n" +
                "길드원33 110\n" +
                "길드원34 100"

        // 총 시도 횟수
        private const val TOTAL_COUNT = 10

        // 최대 당첨 횟수
        private const val MAX_CHOSEN_COUNT = 2

        // 높은 기여도 기준
        private const val HIGH_VALUE_CONDITION = 400

        // 높은 기여도 당첨 횟수
        private const val HIGH_VALUE_COUNT = 2

    }



    // 불러온 멤버와 기여도 목록
    private var memberList: Array<Member> = arrayOf()

    // 시도 횟수
    private var tryCount = 0


    constructor() {

        println("--- 초월 수정 뽑기를 시작합니다! ---")
        println("- 진행 날짜 : " + getCurrentTimeString())
        println("- 뽑기 횟수 : $TOTAL_COUNT (공통 뽑기 횟수 " + (TOTAL_COUNT - HIGH_VALUE_COUNT) + ", 보정 뽑기 횟수 $HIGH_VALUE_COUNT)")
        println("- 보정 뽑기는 기여도 $HIGH_VALUE_CONDITION 이상 멤버를 대상으로 진행하는 뽑기입니다")
        println("- 최대 당첨 횟수 : $MAX_CHOSEN_COUNT (초과 당첨 시 재투표)")
        println("--- 각 확률은 3번째 자리에서 반올림되어 총 합이 100%가 아닐 수 있음 ---")
        println()

    }

    private fun getCurrentTimeString() : String {
        val cal = Calendar.getInstance()
        return "" + cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH) + 1) + "월 " + cal.get(Calendar.DAY_OF_MONTH) + "일 " +
                cal.get(Calendar.HOUR_OF_DAY) + "시 " + cal.get(Calendar.MINUTE) + "분 " + cal.get(Calendar.SECOND) + "초"
    }


    fun read() {

        // 읽기 및 정렬
        memberList = INPUT.replace("\r\n", "|").replace("\n", "|").split("|").map {
            val comp = it.split(" ")
            Member(comp[0], comp[1].toInt())
        }.sortedByDescending { it.value }.toTypedArray()

    }

    fun makeTable() {

        println("--- 각 멤버별 기여도, 일반 뽑기 확률, 보정 뽑기 확률 ---")

        // 확률 계산 및 출력
        val valueSum = memberList.sumBy { it.value }
        val highValueSum = memberList.filter { it.value >= HIGH_VALUE_CONDITION }.sumBy { it.value }
        memberList.forEach {
            it.chance = it.value.toDouble() / valueSum
            if(it.value >= HIGH_VALUE_CONDITION) {
                it.highChance = it.value.toDouble() / highValueSum
                println("- " + it.name + " : " + it.value + " (" + String.format("%.2f%%", it.chance * 100) + ", " + String.format("%.2f%%", it.highChance * 100) + ")")

            } else {
                it.highChance = 0.0
                println("- " + it.name + " : " + it.value + " (" + String.format("%.2f%%", it.chance * 100) + ")")
            }
        }

        println()

    }


    fun electUntilEnd() {

        println("--- 뽑기 시작 ---")
        println()

        while(tryCount < TOTAL_COUNT) {
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
        if(tryCount > (TOTAL_COUNT - HIGH_VALUE_COUNT)) {
            println("--- " + tryCount + "번째 뽑기 (보정 뽑기) ---")
        } else {
            println("--- " + tryCount + "번째 뽑기 ---")
        }

        val seed = System.currentTimeMillis()
        println("Seed : $seed")

        val random = Random(seed)
        val randomValue = random.nextDouble()
        println("랜덤 값 : $randomValue")

        var cursor = 0.0
        var chance = 0.0
        for(member in memberList) {

            chance = if(tryCount > (TOTAL_COUNT - HIGH_VALUE_COUNT)) {
                member.highChance
            } else {
                member.chance
            }

            if(cursor + chance > randomValue) {
                println("당첨 : " + member.name)
                if(member.pickedCount >= MAX_CHOSEN_COUNT) {
                    println("그러나 최대 당첨 횟수에 도달하여 재투표합니다.")
                    tryCount--
                } else {
                    member.pickedCount++
                }
                break
            } else {
                cursor += chance
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
