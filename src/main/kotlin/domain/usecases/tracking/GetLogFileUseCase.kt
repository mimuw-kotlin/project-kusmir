package domain.usecases.tracking

import java.io.File

class GetLogFileUseCase {
    private fun getMtgoRootDirectoryPath(userName: String) = "C:\\Users\\$userName\\AppData\\Local\\Apps\\2.0"

    operator fun invoke(): File {
        val userName: String = System.getProperty("user.name")

        val root = File(getMtgoRootDirectoryPath(userName))

        /*
            I don't really understand where the logs are stored.
            According to personal tests, the path patterns follow:
                [mtgoRootDirectory]\
                    [some random letters]\
                        [more random letters]\
                            mtgo..tion_[a lot of random characters]\
                                Logs\
                                    mtgo

             Sometimes when game starts, new directory is created for the session, sometimes
             logs are appended to the file from previous session.

             How this should be resolved remains a task for the future, for now we're
             looking for the path as described above and take one that was last modified
         */

        val subdirectories =
            root
                .list()
                ?.filter { it != "Data" && File(root, it).isDirectory }
                ?.map { File(root, it) }

        val logFile =
            subdirectories
                ?.flatMap { file -> file.walk().toList() }
                ?.filter {
                    it.isFile &&
                        it.name == "mtgo.log" &&
                        it.path.contains("mtgo..tion_")
                }?.maxByOrNull { it.lastModified() }!!

        return logFile
    }
}
