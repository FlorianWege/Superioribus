package moonlightflower.com.superioribus2.main;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moonlightflower.com.superioribus2.shared.Util;

public class AccountExpandableListAdapter implements ExpandableListAdapter {
    private Context _context;

    public interface Listener {
        void onEntryClicked(Account acc);
    }

    private Listener _listener = null;

    private AccountManager _accountManager;

    public static class TypeNode {
        public static class EntryNode {
            private Account _acc;

            public Account getAcc() {
                return _acc;
            }

            public EntryNode(Account acc) {
                _acc = acc;
            }
        }

        private List<EntryNode> _entries;

        public List<EntryNode> getEntries() {
            return _entries;
        }

        private AuthenticatorDescription _description;

        public AuthenticatorDescription getDescription() {
            return _description;
        }

        public TypeNode(AuthenticatorDescription description) {
            _description = description;
        }
    }

    private List<TypeNode> _types;

    public List<TypeNode> getTypes() {
        return _types;
    }

    public AccountExpandableListAdapter(Context context, Listener listener) throws Util.PermissionException {
        _context = context;
        _listener = listener;

        try {
            _accountManager = AccountManager.get(_context);

            AuthenticatorDescription typeDescriptors[] = _accountManager.getAuthenticatorTypes();

            _types = new ArrayList<>();;

            for (int i = 0; i < typeDescriptors.length; i++) {
                TypeNode typeNode = new TypeNode(typeDescriptors[i]);

                Account accs[] = _accountManager.getAccountsByType(typeDescriptors[i].type);

                if (accs.length > 0) {
                    _types.add(typeNode);

                    typeNode._entries = new ArrayList<>();

                    for (int j = 0; j < accs.length; j++) {
                        typeNode._entries.add(new TypeNode.EntryNode(accs[j]));
                    }
                }
            }
        } catch (SecurityException e) {
            throw new Util.PermissionException(e);
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return getTypes().size();
    }

    @Override
    public int getChildrenCount(int i) {
        Object group = getGroup(i);

        if (group instanceof TypeNode) {
            return ((TypeNode) group).getEntries().size();
        }

        return 0;
    }

    @Override
    public Object getGroup(int groupIndex) {
        return getTypes().get(groupIndex);
    }

    @Override
    public Object getChild(int groupIndex, int childIndex) {
        Object group = getGroup(groupIndex);

        if (group instanceof TypeNode) {
            return ((TypeNode) group).getEntries().get(childIndex);
        }

        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private Map<Integer, View> _groupViews = new HashMap<>();

    @Override
    public View getGroupView(int groupIndex, boolean isExpanded, View view, ViewGroup viewGroup) {
        Object group = getGroup(groupIndex);
        View groupView = null;

        if (group instanceof TypeNode) {
            final TypeNode type = (TypeNode) group;

            final AccountTypeView typeView = new AccountTypeView(_context, type.getDescription(), new AccountTypeView.Listener() {
            });

            //typeView.setExpandedStyle(isExpanded);

            groupView = typeView;
        }

        _groupViews.put(groupIndex, groupView);

        return groupView;
    }

    private Map<Pair<Integer, Integer>, View> _childViews = new HashMap<>();

    @Override
    public View getChildView(int groupIndex, int childIndex, boolean b, View view, ViewGroup viewGroup) {
        Object child = getChild(groupIndex, childIndex);
        View childView = null;

        if (child instanceof TypeNode.EntryNode) {
            childView = new AccountView(_context, ((TypeNode.EntryNode) child).getAcc(), new AccountView.Listener() {
                @Override
                public void onClick(Account acc) {
                    if (_listener != null) {
                        _listener.onEntryClicked(acc);
                    }
                }
            });
        }

        _childViews.put(Pair.create(groupIndex, childIndex), childView);

        return childView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return (getGroupCount() == 0);
    }

    @Override
    public void onGroupExpanded(int groupIndex) {
        View view = _groupViews.get(groupIndex);

        if (view instanceof AccountTypeView) {
            //((VocabListSetView) view).setExpandedStyle(true);
        }
    }

    @Override
    public void onGroupCollapsed(int groupIndex) {
        View view = _groupViews.get(groupIndex);

        if (view instanceof AccountTypeView) {
            //((VocabListSetView) view).setExpandedStyle(false);
        }
    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
}
