/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
   @Author: Arthur L - Refactored command content into modules
*/
package client.command.commands.gm2;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import server.maps.FieldLimit;
import server.maps.MapleMap;
import server.maps.MaplePortal;
import server.maps.MapleMiniDungeonInfo;

public class WarpCommand extends Command {
    {
        setDescription("");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage("Syntax: !warp <mapid>");
            return;
        }

        System.out.println("Execute warp command mapID:" + params[0]);
        try {
            MapleMap targetMap = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
            if (targetMap == null) {
                player.yellowMessage("Map ID " + params[0] + " is invalid.");
                return;
            }
            
            if (!player.isAlive()) {
                player.dropMessage(1, "This command cannot be used when you're dead.");
                return;
            }
            
            if (!player.isGM()) {
                if (player.getEventInstance() != null || MapleMiniDungeonInfo.isDungeonMap(player.getMapId()) || FieldLimit.CANNOTMIGRATE.check(player.getMap().getFieldLimit())) {
                    player.dropMessage(1, "This command cannot be used in this map.");
                    return;
                }
            }
            
            // expedition issue with this command detected thanks to Masterrulax
            player.saveLocationOnWarp();

            if(params.length >= 2){
                System.out.println("portalID:" + params[1]);
                MaplePortal targetPortal = targetMap.getPortal(Integer.parseInt(params[1]));
                System.out.println("targetPortal.getPosition():" + targetPortal.getPosition());
                player.changeMap(targetMap, targetPortal);
            }
            else{
                player.changeMap(targetMap, targetMap.getRandomPlayerSpawnpoint());
            }
        } catch (Exception ex) {
            System.out.println("Error:" + ex.getMessage());
            player.yellowMessage("Map ID " + params[0] + " is invalid.");
        }
    }
}
