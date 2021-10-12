package world.bentobox.limits.listeners;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import world.bentobox.limits.Limits;

public class LimitsPermChangeListener implements Listener {

    private final LuckPerms luckPerms;
    private final Limits addon;

    public LimitsPermChangeListener(Limits addon, LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
        this.addon = addon;
    }

    public void register() {
        EventBus eventBus = this.luckPerms.getEventBus();
        eventBus.subscribe(this.addon, NodeAddEvent.class, this::onNodeAdd);
        eventBus.subscribe(this.addon, NodeRemoveEvent.class, this::onNodeRemove);
    }

    private void onNodeAdd(NodeAddEvent e) {
        if (!e.isUser()) {
            return;
        }

        User target = (User) e.getTarget();
        Node node = e.getNode();

        // LuckPerms events are posted async, we want to process on the server thread!
        this.addon.getServer().getScheduler().runTask((Plugin) this.addon, () -> {
            Player player = this.addon.getServer().getPlayer(target.getUniqueId());
            if (player == null) {
                return; // Player not online.
            }

            if (node instanceof PermissionNode) {
                String permission = ((PermissionNode) node).getPermission();
                player.sendMessage(ChatColor.YELLOW + "You were given the " + permission + " permission!");

            } else if (node instanceof InheritanceNode) {
                String groupName = ((InheritanceNode) node).getGroupName();
                player.sendMessage(ChatColor.YELLOW + "You were added to the " + groupName + " group!");

            } else if (node instanceof PrefixNode) {
                String prefix = ((PrefixNode) node).getMetaValue();
                player.sendMessage(ChatColor.YELLOW + "You were given the " + prefix + " prefix!");

            } else if (node instanceof SuffixNode) {
                String suffix = ((SuffixNode) node).getMetaValue();
                player.sendMessage(ChatColor.YELLOW + "You were given the " + suffix + " suffix!");
            }
        });
    }

    private void onNodeRemove(NodeRemoveEvent e) {
        if (!e.isUser()) {
            return;
        }

        User target = (User) e.getTarget();
        Node node = e.getNode();

        // LuckPerms events are posted async, we want to process on the server thread!
        this.addon.getServer().getScheduler().runTask((Plugin) this.addon, () -> {
            Player player = this.addon.getServer().getPlayer(target.getUniqueId());
            if (player == null) {
                return; // Player not online.
            }

            if (node instanceof PermissionNode) {
                String permission = ((PermissionNode) node).getPermission();
                player.sendMessage(ChatColor.DARK_RED + "You no longer have the " + permission + " permission!");

            } else if (node instanceof InheritanceNode) {
                String groupName = ((InheritanceNode) node).getGroupName();
                player.sendMessage(ChatColor.DARK_RED + "You are no longer in the " + groupName + " group!");

            } else if (node instanceof PrefixNode) {
                String prefix = ((PrefixNode) node).getMetaValue();
                player.sendMessage(ChatColor.DARK_RED + "You no longer have the " + prefix + " prefix!");

            } else if (node instanceof SuffixNode) {
                String suffix = ((SuffixNode) node).getMetaValue();
                player.sendMessage(ChatColor.DARK_RED + "You no longer have the " + suffix + " suffix!");
            }
        });
    }

}