package com.example.localization

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

val LocalAppStrings = staticCompositionLocalOf { Translations.english }
val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

object LocalizationManager {

    fun getStrings(language: AppLanguage): AppStrings {
        return when (language) {
            AppLanguage.ENGLISH -> Translations.english
            AppLanguage.SPANISH -> Translations.spanish
            AppLanguage.FRENCH -> Translations.french
            AppLanguage.GERMAN -> Translations.german
            AppLanguage.HINDI -> Translations.hindi
            AppLanguage.JAPANESE -> Translations.japanese
            AppLanguage.CHINESE -> Translations.chinese
            AppLanguage.ARABIC -> Translations.arabic
            AppLanguage.PORTUGUESE -> Translations.portuguese
            AppLanguage.RUSSIAN -> Translations.russian
            AppLanguage.ITALIAN -> Translations.italian
            AppLanguage.KOREAN -> Translations.korean
            AppLanguage.TAMIL -> Translations.tamil
        }
    }

    /**
     * Translates dynamic user category names, priority tags, and status labels
     * to the user's selected language.
     */
    fun translateDynamic(text: String, language: AppLanguage): String {
        if (language == AppLanguage.ENGLISH) return text

        val lower = text.trim().lowercase(Locale.ROOT)
        return when (language) {
            AppLanguage.SPANISH -> when (lower) {
                "work", "workspace" -> "Trabajo"
                "personal" -> "Personal"
                "health", "fitness" -> "Salud"
                "finance", "budget" -> "Finanzas"
                "urgent", "high" -> "Urgente"
                "medium" -> "Media"
                "low" -> "Baja"
                "completed" -> "Completada"
                "pending" -> "Pendiente"
                "in progress" -> "En progreso"
                "meeting" -> "Reunión"
                "focus" -> "Enfoque"
                "habit", "habits" -> "Hábitos"
                "goal", "goals" -> "Metas"
                "shopping" -> "Compras"
                "travel" -> "Viajes"
                else -> text
            }
            AppLanguage.FRENCH -> when (lower) {
                "work", "workspace" -> "Travail"
                "personal" -> "Personnel"
                "health", "fitness" -> "Santé"
                "finance", "budget" -> "Finances"
                "urgent", "high" -> "Urgent"
                "medium" -> "Moyen"
                "low" -> "Faible"
                "completed" -> "Terminé"
                "pending" -> "En attente"
                "in progress" -> "En cours"
                "meeting" -> "Réunion"
                "focus" -> "Focus"
                "habit", "habits" -> "Habitudes"
                "goal", "goals" -> "Objectifs"
                "shopping" -> "Courses"
                "travel" -> "Voyages"
                else -> text
            }
            AppLanguage.GERMAN -> when (lower) {
                "work", "workspace" -> "Arbeit"
                "personal" -> "Privat"
                "health", "fitness" -> "Gesundheit"
                "finance", "budget" -> "Finanzen"
                "urgent", "high" -> "Dringend"
                "medium" -> "Mittel"
                "low" -> "Niedrig"
                "completed" -> "Erledigt"
                "pending" -> "Ausstehend"
                "in progress" -> "In Bearbeitung"
                "meeting" -> "Meeting"
                "focus" -> "Fokus"
                "habit", "habits" -> "Gewohnheiten"
                "goal", "goals" -> "Ziele"
                "shopping" -> "Einkaufen"
                "travel" -> "Reisen"
                else -> text
            }
            AppLanguage.HINDI -> when (lower) {
                "work", "workspace" -> "कार्यक्षेत्र"
                "personal" -> "व्यक्तिगत"
                "health", "fitness" -> "स्वास्थ्य"
                "finance", "budget" -> "वित्त"
                "urgent", "high" -> "अति आवश्यक"
                "medium" -> "मध्यम"
                "low" -> "सामान्य"
                "completed" -> "पूर्ण"
                "pending" -> "लंबित"
                "in progress" -> "जारी"
                "meeting" -> "बैठक"
                "focus" -> "फोकस"
                "habit", "habits" -> "आदतें"
                "goal", "goals" -> "लक्ष्य"
                "shopping" -> "खरीदारी"
                "travel" -> "यात्रा"
                else -> text
            }
            AppLanguage.JAPANESE -> when (lower) {
                "work", "workspace" -> "仕事"
                "personal" -> "個人"
                "health", "fitness" -> "健康"
                "finance", "budget" -> "財務"
                "urgent", "high" -> "緊急"
                "medium" -> "中"
                "low" -> "低"
                "completed" -> "完了"
                "pending" -> "保留中"
                "in progress" -> "進行中"
                "meeting" -> "会議"
                "focus" -> "集中"
                "habit", "habits" -> "習慣"
                "goal", "goals" -> "目標"
                "shopping" -> "買い物"
                "travel" -> "旅行"
                else -> text
            }
            AppLanguage.CHINESE -> when (lower) {
                "work", "workspace" -> "工作"
                "personal" -> "个人"
                "health", "fitness" -> "健康"
                "finance", "budget" -> "财务"
                "urgent", "high" -> "紧急"
                "medium" -> "中等"
                "low" -> "普通"
                "completed" -> "已完成"
                "pending" -> "待处理"
                "in progress" -> "进行中"
                "meeting" -> "会议"
                "focus" -> "专注"
                "habit", "habits" -> "习惯"
                "goal", "goals" -> "目标"
                "shopping" -> "购物"
                "travel" -> "行程"
                else -> text
            }
            AppLanguage.ARABIC -> when (lower) {
                "work", "workspace" -> "العمل"
                "personal" -> "شخصي"
                "health", "fitness" -> "الصحة"
                "finance", "budget" -> "المالية"
                "urgent", "high" -> "عاجل"
                "medium" -> "متوسط"
                "low" -> "منخفض"
                "completed" -> "مكتمل"
                "pending" -> "قيد الانتظار"
                "in progress" -> "قيد التنفيذ"
                "meeting" -> "اجتماع"
                "focus" -> "تركيز"
                "habit", "habits" -> "عادات"
                "goal", "goals" -> "أهداف"
                "shopping" -> "تسوق"
                "travel" -> "سفر"
                else -> text
            }
            AppLanguage.PORTUGUESE -> when (lower) {
                "work", "workspace" -> "Trabalho"
                "personal" -> "Pessoal"
                "health", "fitness" -> "Saúde"
                "finance", "budget" -> "Finanças"
                "urgent", "high" -> "Urgente"
                "medium" -> "Média"
                "low" -> "Baixa"
                "completed" -> "Concluído"
                "pending" -> "Pendente"
                "in progress" -> "Em andamento"
                "meeting" -> "Reunião"
                "focus" -> "Foco"
                "habit", "habits" -> "Hábitos"
                "goal", "goals" -> "Metas"
                "shopping" -> "Compras"
                "travel" -> "Viagem"
                else -> text
            }
            AppLanguage.RUSSIAN -> when (lower) {
                "work", "workspace" -> "Работа"
                "personal" -> "Личное"
                "health", "fitness" -> "Здоровье"
                "finance", "budget" -> "Финансы"
                "urgent", "high" -> "Срочно"
                "medium" -> "Средний"
                "low" -> "Низкий"
                "completed" -> "Выполнено"
                "pending" -> "В ожидании"
                "in progress" -> "В процессе"
                "meeting" -> "Встреча"
                "focus" -> "Фокус"
                "habit", "habits" -> "Привычки"
                "goal", "goals" -> "Цели"
                "shopping" -> "Покупки"
                "travel" -> "Поездки"
                else -> text
            }
            AppLanguage.ITALIAN -> when (lower) {
                "work", "workspace" -> "Lavoro"
                "personal" -> "Personale"
                "health", "fitness" -> "Salute"
                "finance", "budget" -> "Finanze"
                "urgent", "high" -> "Urgente"
                "medium" -> "Media"
                "low" -> "Bassa"
                "completed" -> "Completato"
                "pending" -> "In attesa"
                "in progress" -> "In corso"
                "meeting" -> "Riunione"
                "focus" -> "Focus"
                "habit", "habits" -> "Abitudini"
                "goal", "goals" -> "Obiettivi"
                "shopping" -> "Spesa"
                "travel" -> "Viaggi"
                else -> text
            }
            AppLanguage.KOREAN -> when (lower) {
                "work", "workspace" -> "업무"
                "personal" -> "개인"
                "health", "fitness" -> "건강"
                "finance", "budget" -> "재정"
                "urgent", "high" -> "긴급"
                "medium" -> "보통"
                "low" -> "낮음"
                "completed" -> "완료됨"
                "pending" -> "대기 중"
                "in progress" -> "진행 중"
                "meeting" -> "회의"
                "focus" -> "몰입"
                "habit", "habits" -> "습관"
                "goal", "goals" -> "목표"
                "shopping" -> "쇼핑"
                "travel" -> "여행"
                else -> text
            }
            AppLanguage.TAMIL -> when (lower) {
                "work", "workspace" -> "வேலை"
                "personal" -> "தனிப்பட்ட"
                "health", "fitness" -> "ஆரோக்கியம்"
                "finance", "budget" -> "நிதி"
                "urgent", "high" -> "அவசரம்"
                "medium" -> "நடுத்தர"
                "low" -> "குறைந்த"
                "completed" -> "முடிந்தது"
                "pending" -> "நிலுவையில்"
                "in progress" -> "செயலில்"
                "meeting" -> "கூட்டம்"
                "focus" -> "கவனம்"
                "habit", "habits" -> "பழக்கங்கள்"
                "goal", "goals" -> "இலக்குகள்"
                "shopping" -> "ஷாப்பிங்"
                "travel" -> "பயணம்"
                else -> text
            }
            else -> text
        }
    }
}
