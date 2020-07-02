# NFCNotes

NFCNotes is a Spigot 1.13+ plugin that allows you to withdraw money using notes. Those notes can also be used to redeem money.

## Requirements

- [Java 8](https://www.java.com/es/download/)
- [Vault](https://github.com/MilkBowl/Vault)
- [Spigot 1.13](https://www.spigotmc.org/) or a newer version. (We recommend [PaperMC](https://papermc.io/) for a better server performance, it also works with Spigot plugins)

## Build NFCNotes

To build NFCNotes, you just need to run `mvn clean package` using Maven.

## NFCNotes Events

NFCNotes includes two events: WithdrawEvent and DepositEvent. If you want to use them, just include NFCNotes in your plugin. We strongly recommend Maven.

```
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.Kikisito</groupId>
            <artifactId>NFCNotes</artifactId>
            <version>master</version>
        </dependency>
    </dependencies>
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[MIT](https://choosealicense.com/licenses/gpl-3.0/)
