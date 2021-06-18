Introduction
============

CHGroovy is an extension to CommandHelper, that adds a single function, groovy() to your scripts. This allows you to dynamically run groovy scripts, from anywhere that CommandHelper can get them from, be it a file on the disk, or player input. This extension is mostly meant for debugging purposes, however, it can be used as a full blown script execution center.

The function takes up to 3 parameters. The first parameter is the script itself. This parameter can be the only one sent, and it will run just fine, for instance:

    groovy('print "Hello World!"')

will print "Hello World!" to the console. Keep in mind that since this is groovy, you can access all parts of the JVM at runtime, which is why this tool is excellent for debugging purposes.

The second parameter is a list of variable bindings to be passed in to groovy's environment. For instance, if you run:

    groovy('print x', array(x: 4))

Then it will print "4" to the console. The last parameter is an array of variable bindings to return. These will be returned as an associative array. For instance:

    @ret = groovy('print x; z = 5', array(x: 4), array('z'))
    msg('Value of z is: '.@ret['z'])

Would print "4" to the console, and msg "5" out to the user. Values are attempted to be converted, if they are primitives, but there are no guarantees that complex values will be interpreted correctly. If this is the case, they will simply be toString'd, and returned as a string.

In Game Interpreter
===================

You can use an in game interpreter, just like CommandHelper's built in /interpreter by using this code:

    /g [$] = >>>
            if($ == ''){
                    export('groovy.status.'.player(), array(
                            multiline: false,
                            buffer: '',
                    ))
                    @pcID = bind(player_chat, null, array(player: player()), @e,
                            @line = @e['message']
                            @status = import('groovy.status.'.player())
                            if(@line == '-'){
                                    unbind()
                                    unbind(import('groovy.player_leave.'.player()))
                                    msg(color('YELLOW').'Now exiting interpreter mode')
                            } else if(@status['multiline'] && @line == '>'.'>>'){
                                    # Entering multiline mode
                                    @status['multiline'] = true
                                    msg(color(YELLOW).'You are now in multiline mode. Type <<< on a line by itself to execute.')
                                    msg(color(GRAY).'>'.'>>')
                            } else if(@status['multiline'] && @line == '<'.'<<'){
                                    @buffer = @status['buffer']
                                    @status['buffer'] = ''
                                    msg(color(GRAY).'<'.'<<')
                                    try(
                                            groovy(@buffer)
                                    , @e, #catch
                                            msg(color(RED).@e[1])
                                    )
                            } else {
                                    if(@status['multiline']){
                                            # Multiline mode, buffer this
                                            msg(color(GRAY).@line)
                                            @status['buffer'] .= @line.'\n'
                                    } else {
                                            # Single line mode, ready to execute this line
                                            msg(color(GRAY).@line)
                                            try(
                                                    groovy(@line)
                                            , @e, #catch
                                                    msg(color(RED).@e[1])
                                            )
                                    }
                            }
                            export('groovy.status.'.player(), @status)
                            # Cancel the chat, since we're consuming it
                            cancel()
                    )
                    export('groovy.player_chat.'.player(), @pcID)
                    @plID = bind(player_quit, null, array(player: player()), @e,
                            unbind(import('groovy.player_chat.'.player()))
                            unbind()
                    )
                    export('groovy.player_leave.'.player(), @plID)
                    msg(color(YELLOW).'You are now in'.color(LIGHT_PURPLE).'Groovy'.color(YELLOW).' interpreter mode.'
                            .' Type a dash (-) on a line by itself to exit, and >>> to enter multiline     mode')
            } else {
                    try(
                        groovy($)
                    , @e, #catch
                        msg(color(RED).@e[1])
                    )
            }
    <<<

If you run /g by itself, you get put into interpreter mode, if you run /g SCRIPT, then it will immediately run the script for you.

For this extension to be useful, you need to know Groovy. The Groovy user guide can be found [here](https://groovy-lang.org/documentation.html).
You can skip any sections that refer to installing and running Groovy. As long as you can run groovy('print "Hello World!"') from a script,
it is correctly set up. As this is an unrestricted shell, it is extremely important that you restrict access to this function. Otherwise,
an attacker could take complete control of your system, since they could potentially run raw, fully functional, unrestricted code on your
machine.
