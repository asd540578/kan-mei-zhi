/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.jrwz.server.serverpackets;

import java.util.ArrayList;

import l1j.jrwz.server.codes.Opcodes;
import l1j.jrwz.server.model.L1Object;
import l1j.jrwz.server.model.L1World;
import l1j.jrwz.server.model.Instance.L1ItemInstance;
import l1j.jrwz.server.model.Instance.L1PcInstance;
import l1j.jrwz.server.templates.L1PrivateShopBuyList;
import l1j.jrwz.server.templates.L1PrivateShopSellList;

// Referenced classes of package l1j.jrwz.server.serverpackets:
// ServerBasePacket

public class S_PrivateShop extends ServerBasePacket {

    public S_PrivateShop(L1PcInstance pc, int objectId, int type) {
        L1Object obj = L1World.getInstance().findObject(objectId);

        if (!(obj instanceof L1PcInstance)) {
            return;
        }
        L1PcInstance shopPc = (L1PcInstance) obj;
        writeC(Opcodes.S_OPCODE_PRIVATESHOPLIST);
        writeC(type);
        writeD(objectId);

        if (type == 0) {
            ArrayList<L1PrivateShopSellList> list = shopPc.getSellList();
            int size = list.size();
            pc.setPartnersPrivateShopItemCount(size);
            writeH(size);
            for (int i = 0; i < size; i++) {
                L1PrivateShopSellList pssl = (L1PrivateShopSellList) list.get(i);
                int itemObjectId = pssl.getItemObjectId();
                int count = pssl.getSellTotalCount() - pssl.getSellCount();
                int price = pssl.getSellPrice();
                L1ItemInstance item = shopPc.getInventory().getItem(itemObjectId);
                if (item != null) {
                    writeC(i);
                    writeC(item.getBless());
                    writeH(item.getItem().getGfxId());
                    writeD(count);
                    writeD(price);
                    writeS(item.getNumberedViewName(count));
                    writeC(0);
                }
            }
        } else if (type == 1) {
            ArrayList<L1PrivateShopBuyList> list = shopPc.getBuyList();
            int size = list.size();
            writeH(size);
            for (int i = 0; i < size; i++) {
                L1PrivateShopBuyList psbl = (L1PrivateShopBuyList) list.get(i);
                int itemObjectId = psbl.getItemObjectId();
                int count = psbl.getBuyTotalCount();
                int price = psbl.getBuyPrice();
                L1ItemInstance item = shopPc.getInventory().getItem(itemObjectId);
                for (L1ItemInstance pcItem : pc.getInventory().getItems()) {
                    if (item.getItemId() == pcItem.getItemId() && item.getEnchantLevel() == pcItem.getEnchantLevel()) {
                        writeC(i);
                        writeD(pcItem.getId());
                        writeD(count);
                        writeD(price);
                    }
                }
            }
        }
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }
}
