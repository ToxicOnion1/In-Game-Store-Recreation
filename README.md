Curiosity Catalog
Based on the game Deadlock
A Java Swing item shop. Browse four tiers of items, buy them with souls, keep them in a 12-slot inventory, and sell them back for a refund.

Features
- Four-tier store drawn over a background image, with hover zoom on each item card
- Souls balance that updates on every purchase and sale
- Inventory panel with an item-count badge, total item value, and a 2×6 slot grid
- Buy by clicking a item card. Owned items get a green border and a dimmed overlay
- Sell by clicking a filled inventory slot. You get a refund and the item card becomes buyable again

Project Structure
src/main/java/com/mycompany/deza/
├── ShopFrame.java        Main window, balance, buy/sell logic, store layout
├── StoreItem.java        A single item card (hover, click-to-buy, owned state)
├── InventoryPanel.java   Inventory strip (badge, total value, slots, sell clicks)
└── BackgroundPanel.java  Panel that draws the background image and places children

src/main/resources/
├── shop_background.png   Store background image
└── icons/                Item icons (optional, one PNG per item)

Running
- Run the main method in ShopFrame.
- The starting balance is 20000 souls.

Action	  How
Buy       an item	Click its card in the store
Sell      an item	Click its slot in the inventory
Check     balance	Top-left of the store, shown as Souls: N
