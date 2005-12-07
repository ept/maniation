#!/usr/bin/perl

$parts = [
    {"input" => "bones1_leftarm",
     "parent" => "",
     "bones" => ["Spine1", "Spine2", "Torso.L", "Shoulder.L", "UpperArm.L",
        "LowerArm.L", "Hand.L", "Fingers.L"]},
        
    {"input" => "bones2_rightarm",
     "parent" => 1,
     "bones" => ["", "", "Torso.R", "Shoulder.R", "UpperArm.R", "LowerArm.R",
        "Hand.R", "Fingers.R"]},
        
    {"input" => "bones3_head",
     "parent" => 1,
     "bones" => ["", "", "Torso", "Head"]},
     
    {"input" => "bones4_leftleg",
     "parent" => "",
     "bones" => ["UpperLeg.L", "LowerLeg.L", "Foot.L"]},
     
    {"input" => "bones5_rightleg",
     "parent" => "",
     "bones" => ["UpperLeg.R", "LowerLeg.R", "Foot.R"]}];

$boneid = 0;

for $part (@$parts) {
    $input = $$part{'input'};
    $parent = $$part{'parent'};
    $bones = $$part{'bones'};
    open(BASES, "bash -c 'echo \"bonebases($input())\" | octave'|");
    open(ORIENTATIONS, "bash -c 'echo \"bones($input())\" | octave'|");
    
    for $bonename (@$bones) {
        if ($bonename ne "") {
            print '    <bone id="'.$boneid.'" name="'.$bonename.'"';
            print ' parent-bone-id="'.$parent.'"' if $parent ne "";
            print ">\n";
        }
        while (<BASES>) {
            next unless /^ *([\-0-9\.]+) +([\-0-9\.]+) +([\-0-9\.]+) *$/;
            last unless $bonename ne "";
            print '      <base x="'.$1.'" y="'.$2.'" z="'.$3.'"/>'."\n";
            last;
        }
        while (<ORIENTATIONS>) {
            next unless /^ *([\-0-9\.]+) +([\-0-9\.]+) +([\-0-9\.]+) +([\-0-9\.]+) *$/;
            last unless $bonename ne "";
            print '      <orientation x="'.$1.'" y="'.$2.'" z="'.$3.'" w="'.$4.'"/>'."\n";
            last;
        }
        if ($bonename ne "") {
            print "    </bone>\n";
            $parent = $boneid;
            $boneid++;
        }
    }
    while (<BASES>) {}
    while (<ORIENTATIONS>) {}
}
