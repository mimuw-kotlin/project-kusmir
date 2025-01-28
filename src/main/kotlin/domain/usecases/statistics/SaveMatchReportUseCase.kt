package domain.usecases.statistics

import domain.model.MatchReport
import domain.repository.MatchReportRepository

class SaveMatchReportUseCase(
    private val matchReportRepository: MatchReportRepository
) {
    suspend operator fun invoke(matchReport: MatchReport): MatchReport {
       if (matchReport.id == -1L) {
           return matchReportRepository.createMatchReport(
               opponentName = matchReport.opponentName,
               date = matchReport.date,
               structure = matchReport.structure,
               format = matchReport.format,
               registeredDeckId = matchReport.registeredDeckId,
               gameReports = matchReport.gameReports
           )
       } else {
           matchReportRepository.updateMatchReport(matchReport)
           return matchReport
       }
    }
}