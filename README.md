werewolfchatcli - the cli version of Werewolfchat Android App
By Matthew Yates
A product of Yates Enterprise Engineering and Technologies (YEET)


Note: This is just the source for a jar that can talk to Werewolf chat android app.
It will NOT set up TOR or I2P for you, you need to configure that your self
TOR_Use_Defaults means socks proxy on localhost:9050
I2P_Use_Defaults means http proxy on localhost:4444

When you make the Jar set com.werewolfchat.werewolf_chat_cli.CryptoWorker as the main class

Here is the help dialog:
Cryptoworker.jar Version 0.2
to print this help message -h or --help
to encrypt: -e key_in_hex string_to_encrypt
to encrypt: --encrypt key_in_hex string_to_encrypt
to print the version: -v
to start interactive mode: -i
to start interactive mode with a key file: -i --File=/path/to/your/key/file
to start interactive mode with a specified user id (will overwrite keyfile): -i  --User_ID=username
to start interactive mode with an output directory: -i --Output_Dir=/path/to/output/directory/

in interactive mode over https with no proxy to the public server is assumed unless you specify a different type
the poxy is assumed to be localhost unless otherwise specified by --Proxy_Server=my.proxy.server.url
TOR:
to start interactive mode over TOR assuming default settings: -i --TOR_Use_Defaults
to start interactive mode over TOR without a proxy: -i --TOR_No_Proxy
to start interactive mode over TOR: -i --TOR_Proxy_SOCKS=9050
to start interactive mode over TOR: -i --TOR_Proxy_HTTP=8118
I2P:
to start interactive mode over TOR assuming default settings: -i --I2P_Use_Defaults
to start interactive mode over I2P without a proxy: -i --I2P_No_Proxy
to start interactive mode over I2P: -i --I2P_Proxy_SOCKS=4444
to start interactive mode over I2P: -i --I2P_Proxy_HTTP=4444
HTTPS with a proxy:
to start interactive mode over HTTPS with a SOCKS proxy -i --HTTPS_Proxy_SOCKS=8118
to start interactive mode over HTTPS with a HTTP proxy -i --HTTPS_Proxy_HTTP=8118
A private server:
to start interactive mode with private server: -i --Private_Server_Url=https://something.com
to start interactive mode with private server with an SOCKS proxy: -i --Private_Server_Url=https://something.com --Private_Server_Proxy_SOCKS=9050
to start interactive mode with private server with an HTTP proxy: -i --Private_Server_Url=https://something.com --Private_Server_Proxy_HTTP=9050
you can't combine the the TOR, I2P, and private_server flags with each other
all other interactive mode flags can be combined as long as the first flag is an -i

to send a message outside of interactive mode: -s --To_Id=idtosendto  --Message=messagetosend
all of the -i flags work for -s mode

to pull messages outside of interactive mode: -pm
all of the -i flags work for -pm mode

to pull the users outside of interactive mode: -u
all of the -i flags work for -pk mode

the -i, -e, -s, -pm, and -u flags can not be used together
the -i, -e, -s, -pm, and -u have to be the first flag

