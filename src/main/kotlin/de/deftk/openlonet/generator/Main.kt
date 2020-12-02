package de.deftk.openlonet.generator

import de.deftk.lonet.api.LoNet
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import kotlin.system.exitProcess

private const val VERSION = "1.0.0"

fun main(args: Array<String>) {
    val options = Options()

    val usernameOption = Option("u", "username", true, "Username (mail)")
    usernameOption.isRequired = false
    options.addOption(usernameOption)

    val passwordOption = Option("p", "password", true, "Password")
    passwordOption.isRequired = false
    options.addOption(passwordOption)

    val titleOption = Option("t", "title", true, "Description of the token (displayed inside online interface)")
    titleOption.isRequired = false
    options.addOption(titleOption)

    val identOption = Option("i", "ident", true, "Identity of the token (displayed inside online interface)")
    identOption.isRequired = false
    options.addOption(identOption)

    val helpOption = Option("h", "help", true, "Shows this help")
    helpOption.isRequired = false
    options.addOption(helpOption)

    val versionOption = Option("v", "version", false, "Shows current program version")
    versionOption.isRequired = false
    options.addOption(versionOption)

    val parser = DefaultParser()
    val formatter = HelpFormatter()

    val cmd = try {
        parser.parse(options, args)
    } catch (e: Exception) {
        formatter.printHelp("Token generator", options)
        exitProcess(-1)
    }

    if (cmd.hasOption("version")) {
        println("Token generator version $VERSION")
    }

    if (cmd.hasOption("help")) {
        formatter.printHelp("Token generator", options)
        exitProcess(0)
    }

    val username = if (cmd.hasOption("username")) {
        cmd.getOptionValue("username")
    } else {
        print("Username (mail): ")
        System.`in`.bufferedReader().readLine()
    }

    val title = if (cmd.hasOption("title")) {
        cmd.getOptionValue("title")
    } else {
        print("Title (displayed in online interface): ")
        System.`in`.bufferedReader().readLine()
    }

    val ident = if (cmd.hasOption("ident")) {
        cmd.getOptionValue("ident")
    } else {
        print("Identity (displayed in online interface): ")
        System.`in`.bufferedReader().readLine()
    }

    val password = if (cmd.hasOption("password")) {
        cmd.getOptionValue("password")
    } else {
        print("Password: ")
        try {
            String(System.console().readPassword())
        } catch (e: Exception) {
            System.`in`.bufferedReader().readLine()
        }
    }

    try {
        println("Generated token: ${generateToken(username, password, title, ident)}")
    } catch (e: Exception) {
        println("Failed to generate token: ${e.message ?: e}")
        e.printStackTrace()
    }
}

private fun generateToken(username: String, password: String, title: String, ident: String): String {
    return LoNet.loginCreateTrust(username, password, title, ident).apply { logout(false) }.authKey
}