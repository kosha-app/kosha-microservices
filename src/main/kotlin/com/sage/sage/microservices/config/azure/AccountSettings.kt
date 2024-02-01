package com.sage.sage.microservices.config.azure

import com.azure.cosmos.implementation.apachecommons.lang.StringUtils


/**
 * Contains the account configurations for Sample.
 *
 * For running tests, you can pass a customized endpoint configuration in one of the following
 * ways:
 *
 *  * -DACCOUNT_KEY="[your-key]" -ACCOUNT_HOST="[your-endpoint]" as JVM
 * command-line option.
 *  * You can set ACCOUNT_KEY and ACCOUNT_HOST as environment variables.
 *
 *
 * If none of the above is set, emulator endpoint will be used.
 * Emulator http cert is self signed. If you are using emulator,
 * make sure emulator https certificate is imported
 * to java trusted cert store:
 * https://docs.microsoft.com/en-us/azure/cosmos-db/local-emulator-export-ssl-certificates
 */
object AccountSettings {
    // Replace MASTER_KEY and HOST with values from your Azure Cosmos DB account.
    // The default values are credentials of the local emulator, which are not used in any production environment.
    // <!--[SuppressMessage("Microsoft.Security", "CS002:SecretInNextLine")]-->
    var MASTER_KEY = System.getProperty(
        "ACCOUNT_KEY",
        StringUtils.defaultString(
            StringUtils.trimToNull(
                System.getenv()["ACCOUNT_KEY"]
            ),
            "ErqYFusppsB7s348T5SvlLK0O6Cs9vpYULNnyH5jHEQqBhlaUBhzzw5nfTlNK3ewjrqPu164A1MrACDbRNXQQQ=="
        )
    )
    var HOST = System.getProperty(
        "ACCOUNT_HOST",
        StringUtils.defaultString(
            StringUtils.trimToNull(
                System.getenv()["ACCOUNT_HOST"]
            ),
            "https://kosha-app.documents.azure.com:443/"
        )
    )
}
