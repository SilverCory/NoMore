/*
 * http://ryred.co/
 * ace[at]ac3-servers.eu
 *
 * =================================================================
 *
 * Copyright (c) 2016, Cory Redmond
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of NoMore nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package co.ryred.nomore;

import co.ryred.red_commons.Logs;
import co.ryred.red_commons.bungee.plugin.BungeePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;

/**
 * Created by Cory Redmond on 20/05/2016.
 *
 * @author Cory Redmond <ace@ac3-servers.eu>
 */
public class NoMorePlugin extends BungeePlugin implements Listener {

	private HashSet<Integer> supportedVersions = new HashSet<>();

	@Override
	public void onLoad() {

		supportedVersions = new HashSet<>();

		saveDefaultConfig();
		Logs.get( NoMorePlugin.class, getLogger(), getConfig().getBoolean( "debug", false ) )._D( "Debugging is enabled!" );

	}

	@Override
	public void onEnable() {

		supportedVersions.addAll( getConfig().getIntList( "supported-versions" ) );

		getProxy().getPluginManager().registerListener( this, this );
		getProxy().getPluginManager().registerCommand( this, new Command( "nomorereload", "nomore.reload" ) {
			@Override
			public void execute( CommandSender sender, String[] strings ) {
				doReload( sender );
			}
		} );

	}

	@Override
	public void onDisable() {

		getProxy().getPluginManager().unregisterCommands( this );
		getProxy().getPluginManager().unregisterListeners( this );

		supportedVersions = null;

	}

	private void doReload( CommandSender sender ) {

		try {
			onDisable();
			reloadConfig();
			onLoad();
			onEnable();
		} catch ( Exception ex ) {
			ex.printStackTrace();
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', "&cError reloading NoMore..." ) );
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', "&c" + ex.getMessage() ) );
		}

		sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', "&eReloaded NoMore." ) );

	}

	@EventHandler
	public void onReload( ProxyReloadEvent event ) {
		doReload( event.getSender() );
	}

	@EventHandler( priority = Byte.MAX_VALUE )
	public void onProxyPing( ProxyPingEvent event ) {

		int currentVersion = event.getResponse().getVersion().getProtocol();
		if( !supportedVersions.contains( currentVersion ) )
			event.getResponse().getVersion().setProtocol( currentVersion + 2 );

	}

}
